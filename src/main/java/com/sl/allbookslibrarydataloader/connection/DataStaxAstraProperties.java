package com.sl.allbookslibrarydataloader.connection;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "datastax.astra")
@Data
public class DataStaxAstraProperties {

    private File secureConnectBundle;
}
