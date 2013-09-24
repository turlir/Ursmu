package ru.ursmu.application.Abstraction;

import java.io.IOException;

public interface IDownloadBehavior {
    public String Download(String uri, String parameters) throws IOException;
}
