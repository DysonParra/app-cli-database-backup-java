/*
 * @fileoverview    {Application}
 *
 * @version         2.0
 *
 * @author          Dyson Arley Parra Tilano <dysontilano@gmail.com>
 *
 * @copyright       Dyson Parra
 * @see             github.com/DysonParra
 *
 * History
 * @version 1.0     Implementation done.
 * @version 2.0     Documentation added.
 */
package com.project.dev;

import com.project.dev.database.DataBaseConfig;
import com.project.dev.frame.BackUpFrame;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TODO: Description of {@code Application}.
 *
 * @author Dyson Parra
 * @since Java 17 (LTS), Gradle 7.3
 */
@EnableScheduling
@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private BackUpFrame backUpFrame;
    private DataBaseConfig dataBaseConfig;
    @Value("${spring.datasource.database}")
    private String database;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
        builder.headless(false).run(args);
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Execution started");
        System.out.println("Database: " + database);
        //System.out.println("Username: " + username);
        //System.out.println("Password: " + password);
        dataBaseConfig = DataBaseConfig.builder()
                .username(username)
                .password(password)
                .database(database)
                .waitTime(3600)
                .build();

        backUpFrame = new BackUpFrame();
        backUpFrame.setVisible(true);
        backUpFrame.setDataBaseConfig(dataBaseConfig);
        startExecution();
    }

    /**
     *
     */
    public void startExecution() {
        while (true) {
            try {
                backUpFrame.addText(makeBackUpFile());
                System.out.println("Another backup will generated in " + dataBaseConfig.getWaitTime() + " seconds");
                synchronized (dataBaseConfig.getSynchronizer()) {
                    dataBaseConfig.getSynchronizer().wait(dataBaseConfig.getWaitTime() * 1000);
                }
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     *
     * @return
     */
    public String makeBackUpFile() {
        String outputFileName = dataBaseConfig.getDatabase() + "_" + DATE_FORMAT.format(new Date()) + ".sql";
        System.out.println("Creating backup with name: " + outputFileName);
        String command = String.format("mysqldump -u%s -p%s --add-drop-table --databases %s -r %s",
                dataBaseConfig.getUsername(),
                dataBaseConfig.getPassword(),
                dataBaseConfig.getDatabase(),
                outputFileName);
        int processComplete = 0;
        try {
            //System.out.println("Command: '" + command + "'");
            Process runtimeProcess = Runtime.getRuntime().exec(command);
            processComplete = runtimeProcess.waitFor();
        } catch (Exception e) {
        }
        String result = (processComplete == 0)
                ? "Se ha generado:  " + outputFileName
                : "Error generando: " + outputFileName;
        System.out.println(result);
        return result;
    }

}
