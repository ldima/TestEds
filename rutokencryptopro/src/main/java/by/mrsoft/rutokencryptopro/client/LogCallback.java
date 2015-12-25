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
package by.mrsoft.rutokencryptopro.client;

import android.content.res.Resources;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import by.mrsoft.rutokencryptopro.Constants;
import by.mrsoft.rutokencryptopro.R;
import ru.CryptoPro.JCP.tools.Encoder;

/**
 * Служебный класс LogCallback предназначен
 * для записи в поле сообщений и установки
 * статуса.
 *
 * 30/05/2013
 *
 */
public class LogCallback {

    /**
     * Поле для записи.
     */
    private EditText logger = null;

    /**
     * Поле для статуса.
     */
    private TextView showStatus = null;

    /**
     * Описание статуса.
     */
    private String statusFieldValue = null;
    private String statusUnknown = null;
    private String statusOK = null;
    private String statusFailed = null;

    /**
     * Конструктор.
     *
     * @param resources Ресурсы приложения.
     * @param log Графический объект для вывода лога.
     * @param status  Графический объект для вывода
     * статуса.
     */
    public LogCallback(Resources resources,
        EditText log, TextView status) {

        /*
        * Ресурсы приложения.
        */

        logger = log;
        showStatus = status;

        statusFieldValue = resources.getString(R.string.StatusField);
        statusUnknown = resources.getString(R.string.StatusUnknown);
        statusOK = resources.getString(R.string.StatusOK);
        statusFailed = resources.getString(R.string.StatusError);
    }

    /**
     * Получение графического объекта для вывода лога.
     *
     * @return объект для вывода лога.
     */
    public EditText getLogger() {
        return logger;
    }

    /**
     * Запись сообщения в поле.
     *
     * @param message Сообщение.
     */
    public void log(final String message) {

/*        if (logger != null) {

            logger.post(new Runnable() {
                public void run() {
                    logger.append("\n" + message);
                }
            });

        } // if
        else {
            Log.i(Constants.APP_LOGGER_TAG, message);
        } // else*/


        Log.d("CryptoPro",message);


    }

    /* Запись сообщения в поле.
     *
     * @param message Сообщение.
     * @param base64 True, если нужно конвертировать
     * в base64.
     */
    public void log(byte[] message, boolean base64) {
        log(base64 ? toBase64(message) : new String(message));
    }

    /**
     * Конвертация в base64.
     *
     * @param data Исходные данные.
     * @return конвертированная строка.
     */
    private String toBase64(byte[] data) {
        Encoder enc = new Encoder();
        return enc.encode(data);
    }

    /**
     * Очистка поля.
     */
    public void clear() {

        if (logger != null) {

            logger.post(new Runnable() {
                public void run() {
                    logger.setText("");
                }
            });

        } // if

        if (showStatus != null) {

            showStatus.post(new Runnable() {
                public void run() {
                    showStatus.setText(statusFieldValue + ": " +
                            statusUnknown);
                }
            });

        } // if
    }

    /**
     * Задание статуса провала.
     *
     */
    public void setStatusFailed() {
        setStatus(statusFailed);
    }

    /**
     * Задание статуса успеха.
     *
     */
    public void setStatusOK() {
        setStatus(statusOK);
    }

    /**
     * Отображение строки статуса.
     *
     * @param status Строка статуса.
     */
    private void setStatus(final String status) {

        if (showStatus != null) {

            showStatus.post(new Runnable() {
                public void run() {
                    showStatus.setText(statusFieldValue +
                        ": " + status);
                }
            });

        } // if
    }

}
