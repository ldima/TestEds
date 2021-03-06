/*
 * Copyright (c) 2015, CJSC Aktiv-Soft. See the LICENSE file at the top-level directory of this distribution.
 * All Rights Reserved.
 */

package ru.rutoken.Pkcs11Caller;

import android.util.Base64;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;

import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;

import javax.security.auth.x500.X500Principal;


import ru.rutoken.Pkcs11.CK_ATTRIBUTE;
import ru.rutoken.Pkcs11.Pkcs11Constants;
import ru.rutoken.Pkcs11.RtPkcs11;
import ru.rutoken.Pkcs11Caller.exception.CertNotFoundException;
import ru.rutoken.Pkcs11Caller.exception.CertParsingException;
import ru.rutoken.Pkcs11Caller.exception.KeyNotFoundException;
import ru.rutoken.Pkcs11Caller.exception.Pkcs11CallerException;
import ru.rutoken.Pkcs11Caller.exception.Pkcs11Exception;

public class Certificate {
    private X500Name mSubject;
    private byte[] mKeyPairId;
    private NativeLong object;

    private String CertInfoId;
    private String CertInfoName;
    private String CertInfoIssuer;
    private Date CertInfoValidUntil;



    public NativeLong getObject() {
        return object;
    }

    public String getCertInfoId() {
        return CertInfoId;
    }

    public String getCertInfoName() {
        return CertInfoName;
    }

    public String getCertInfoIssuer() {
        return CertInfoIssuer;
    }

    public Date getCertInfoValidUntil() {
        return CertInfoValidUntil;
    }

    public Certificate(RtPkcs11 pkcs11, NativeLong session, NativeLong object)
            throws Pkcs11CallerException {

        this.object = object;
        CK_ATTRIBUTE[] attributes = (CK_ATTRIBUTE[]) (new CK_ATTRIBUTE()).toArray(3);
        attributes[0].type = Pkcs11Constants.CKA_SUBJECT;
        attributes[1].type = Pkcs11Constants.CKA_VALUE;
        attributes[2].type = Pkcs11Constants.CKA_ID;

        NativeLong rv = pkcs11.C_GetAttributeValue(session, object,
                attributes, new NativeLong(attributes.length));
        if (!rv.equals(Pkcs11Constants.CKR_OK)) throw Pkcs11Exception.exceptionWithCode(rv);

        for (CK_ATTRIBUTE attr : attributes) {
            attr.pValue = new Memory(attr.ulValueLen.intValue());

        }

        rv = pkcs11.C_GetAttributeValue(session, object,
                attributes, new NativeLong(attributes.length));
        if (!rv.equals(Pkcs11Constants.CKR_OK)) throw Pkcs11Exception.exceptionWithCode(rv);

        byte[] subjectValue =
                attributes[0].pValue.getByteArray(0, attributes[0].ulValueLen.intValue());
        mSubject = X500Name.getInstance(subjectValue);
        if (mSubject == null) throw new CertNotFoundException();

        byte[] keyValue = null;
        try {
            X509CertificateHolder certificateHolder = new X509CertificateHolder(
                    attributes[1].pValue.getByteArray(0, attributes[1].ulValueLen.intValue()));
            SubjectPublicKeyInfo publicKeyInfo = certificateHolder.getSubjectPublicKeyInfo();
            keyValue = publicKeyInfo.parsePublicKey().getEncoded();
        } catch (IOException exception) {
            throw new CertParsingException();
        }


        try {
        CertInfoId = android.util.Base64.encodeToString(attributes[2].pValue.getByteArray(0, attributes[2].ulValueLen.intValue()), Base64.NO_WRAP);
        // создание объекта для работы с сертификатами типа X509
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)certFactory.generateCertificate(new ByteArrayInputStream(attributes[1].pValue.getByteArray(0, attributes[1].ulValueLen.intValue())));
        // вывод серийного номера сертификата на экран
        CertInfoName = getCNforCertificate(cert.getSubjectX500Principal());
        CertInfoIssuer = getCNforCertificate(cert.getIssuerX500Principal());

        //String validUntil = ServiceToConvert.dateToViewShort.format(cert.getNotAfter());
        //CertInfoValidUntil = " " + EOSmobileConvertDate.getInstance().getStringInFormatShort3(cert.getNotAfter());
        CertInfoValidUntil = cert.getNotAfter();

           // this.certInfo  = new CertInfo(name, issuer, validUntil, id);

        } catch (Exception exception) {
            throw new CertParsingException();
        }




        if (keyValue == null) throw new KeyNotFoundException();

        // уберём заголовок ключа (первые 2 байта)
        keyValue = Arrays.copyOfRange(keyValue, 2, keyValue.length);
        CK_ATTRIBUTE[] template = (CK_ATTRIBUTE[]) (new CK_ATTRIBUTE()).toArray(2);

        final NativeLongByReference keyClass =
                new NativeLongByReference(Pkcs11Constants.CKO_PUBLIC_KEY);
        template[0].type = Pkcs11Constants.CKA_CLASS;
        template[0].pValue = keyClass.getPointer();
        template[0].ulValueLen = new NativeLong(NativeLong.SIZE);

        ByteBuffer valueBuffer = ByteBuffer.allocateDirect(keyValue.length);
        valueBuffer.put(keyValue);
        template[1].type = Pkcs11Constants.CKA_VALUE;
        template[1].pValue = Native.getDirectBufferPointer(valueBuffer);
        template[1].ulValueLen = new NativeLong(keyValue.length);

        NativeLong pubKeyHandle = findObject(pkcs11, session, template);
        if (pubKeyHandle == null) throw new KeyNotFoundException();

        CK_ATTRIBUTE[] idTemplate = (CK_ATTRIBUTE[]) (new CK_ATTRIBUTE()).toArray(1);
        idTemplate[0].type = Pkcs11Constants.CKA_ID;

        rv = pkcs11.C_GetAttributeValue(session, pubKeyHandle,
                idTemplate, new NativeLong(idTemplate.length));
        if (!rv.equals(Pkcs11Constants.CKR_OK)) throw Pkcs11Exception.exceptionWithCode(rv);

        idTemplate[0].pValue = new Memory(idTemplate[0].ulValueLen.intValue());

        rv = pkcs11.C_GetAttributeValue(session, pubKeyHandle,
                idTemplate, new NativeLong(idTemplate.length));
        if (!rv.equals(Pkcs11Constants.CKR_OK)) throw Pkcs11Exception.exceptionWithCode(rv);

        mKeyPairId = idTemplate[0].pValue.getByteArray(0, idTemplate[0].ulValueLen.intValue());
    }

    private String getCNforCertificate (X500Principal inputObject){
        String[] split = inputObject.getName().split(",");
        for(String certField : split)
        {
            if(certField.startsWith("CN"))
                // вывод common name сертификата на экран
                // publishProgress("\tCertificate CN: " + certField.substring(certField.indexOf('=')+1) + "\n");
                return certField.substring(certField.indexOf('=')+1);
        }
        return new String();
    }


    public X500Name getSubject() {
        return mSubject;
    }

    public NativeLong getPrivateKeyHandle(RtPkcs11 pkcs11, NativeLong session)
            throws Pkcs11CallerException {
        CK_ATTRIBUTE[] template = (CK_ATTRIBUTE[]) (new CK_ATTRIBUTE()).toArray(2);

        final NativeLongByReference keyClass =
                new NativeLongByReference(Pkcs11Constants.CKO_PRIVATE_KEY);
        template[0].type = Pkcs11Constants.CKA_CLASS;
        template[0].pValue = keyClass.getPointer();
        template[0].ulValueLen = new NativeLong(NativeLong.SIZE);

        ByteBuffer idBuffer = ByteBuffer.allocateDirect(mKeyPairId.length);
        idBuffer.put(mKeyPairId);
        template[1].type = Pkcs11Constants.CKA_ID;
        template[1].pValue = Native.getDirectBufferPointer(idBuffer);
        template[1].ulValueLen = new NativeLong(mKeyPairId.length);

        return findObject(pkcs11, session, template);
    }

    private NativeLong findObject(RtPkcs11 pkcs11, NativeLong session, CK_ATTRIBUTE[] template)
        throws Pkcs11CallerException {
        NativeLong rv = pkcs11.C_FindObjectsInit(session,
                template, new NativeLong(template.length));
        if (!rv.equals(Pkcs11Constants.CKR_OK)) throw Pkcs11Exception.exceptionWithCode(rv);

        NativeLong objects[] = new NativeLong[1];
        NativeLongByReference count =
                new NativeLongByReference(new NativeLong(objects.length));
        rv = pkcs11.C_FindObjects(session, objects, new NativeLong(objects.length),
                count);

        NativeLong rv2 = pkcs11.C_FindObjectsFinal(session);
        if (!rv.equals(Pkcs11Constants.CKR_OK)) throw Pkcs11Exception.exceptionWithCode(rv);
        else if (!rv2.equals(Pkcs11Constants.CKR_OK)) throw Pkcs11Exception.exceptionWithCode(rv2);
        else if (count.getValue().intValue() <= 0) return null;

        return objects[0];
    }
}
