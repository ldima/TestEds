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
package by.mrsoft.rutokencryptopro.client.example.interfaces;

import by.mrsoft.rutokencryptopro.client.LogCallback;

/**
 * Служебный интерфейс IThreadExecuted предназначен
 * для выполнения задачи внутри потока. Обычно это
 * задача передачи или получения данных по сети
 * интернет.
 *
 * 29/05/2013
 *
 */
public interface IThreadExecuted {

    /**
     * Метод для выполнения задачи в потоке.
     * Задача записывается внутри метода.
     *
     * @param callback Логгер.
     */
    public void execute(LogCallback callback);

}
