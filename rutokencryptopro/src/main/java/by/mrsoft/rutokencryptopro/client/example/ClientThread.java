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

import android.os.Looper;

import by.mrsoft.rutokencryptopro.client.LogCallback;
import by.mrsoft.rutokencryptopro.client.example.interfaces.IThreadExecuted;

/**
 * Служебный класс ClientThread выполняет задачу
 * в отдельном потоке.
 *
 * 29/05/2013
 *
 */
public class ClientThread extends Thread {

    /**
     * Выполняемая задача.
     */
    private IThreadExecuted executedTask = null;

    /**
     * Логгер.
     */
    private LogCallback logCallback = null;

    /**
     * Конструктор.
     *
     * @param task Выполняемая задача.
     */
    public ClientThread(LogCallback callback,
        IThreadExecuted task) {

        logCallback = callback;
        executedTask = task;
    }

    /**
     * Поточная функция. Запускает выполнение
     * задания. В случае ошибки пишет сообщение
     * в лог.
     *
     */
    @Override
    public void run() {

        /**
         * Обязательно зададим, т.к. может потребоваться
         * ввод пин-кода в окне.
         */
        Looper.getMainLooper().prepare();

        /**
         * Выполняем задачу.
         */
        executedTask.execute(logCallback);

    }

}
