/**
 * Copyright 2004-2013 Crypto-Pro. All rights reserved.
 * Этот файл содержит информацию, являющуюся
 * собственностью компании Крипто-Про.
 *
 * Любая часть этого файла не может быть скопирована,
 * исправлена, переведена на другие языки,
 * локализована или модифицирована любым способом,
 * откомпилирована, передана по сети с или на
 * любую компьютерную систему без предварительного
 * заключения соглашения с компанией Крипто-Про.
 */
package by.mrsoft.rutokencryptopro.client.example;

import android.os.AsyncTask;

import by.mrsoft.rutokencryptopro.client.LogCallback;
import by.mrsoft.rutokencryptopro.client.example.interfaces.ContainerAdapter;
import by.mrsoft.rutokencryptopro.client.example.interfaces.ISignData;
import by.mrsoft.rutokencryptopro.util.KeyStoreType;

/**
 * Класс PrintCertificateContentExample реализует пример
 * вывода содержимого сертификата в лог.
 *
 * 25/07/2013
 *
 */
public class PrintCertificateContentExample extends ISignData {

    /**
     * Конструктор.
     *
     * @param adapter Настройки примера.
     */
    public PrintCertificateContentExample(ContainerAdapter adapter) {
        super(adapter, false);
    }

    @Override
    public void getResult(LogCallback callback) throws Exception {

        // Тип контейнера по умолчанию.
        String keyStoreType = KeyStoreType.currentType();
        callback.log("Default container type: " + keyStoreType);
        callback.log("Load source key container.");

        load(true, keyStoreType, containerAdapter.getClientAlias(),
                containerAdapter.getClientPassword(), callback);

        if (getCertificate() == null) {
            callback.log("Source certificate is null.");
            return;
        } // if

        callback.log("*************************");
        callback.log(getCertificate().toString());

        callback.setStatusOK();
    }
}
