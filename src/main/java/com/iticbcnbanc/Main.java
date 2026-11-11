package com.iticbcnbanc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;
 
 
public class Main {
    public static void main(String[] args) {
        Properties prop = new Properties();

        try {
            prop.load(Files.newBufferedReader(Path.of("banc","src","main","resources","com.properties")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        HikariConfig hkcfg = new HikariConfig();
//parametria de la connexió
        hkcfg.setJdbcUrl(prop.getProperty("DB_URL"));
        hkcfg.setUsername(prop.getProperty("DB_USER"));
        hkcfg.setPassword(prop.getProperty("DB_PASSWORD"));
        hkcfg.setMaximumPoolSize(Integer.parseInt(prop.getProperty("DB_MAX_POOL")));
 
 
//iniciem el datasource i controlem excepcions
 
 
 
 
        try (HikariDataSource hkds = new HikariDataSource(hkcfg)) {
//connectem la base de dades
            try (Connection cnn = hkds.getConnection()) {
              String query = "SELECT * FROM statements";

              PreparedStatement ps = cnn.prepareStatement(query);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        String resultat = String.join(";",
                                rs.getString("id_statement"),
                                rs.getString("tipus_moviment"),
                                rs.getString("import"),
                                rs.getString("divisa")
                            );
                        System.out.println("moviment amb id: " + resultat);
                        }
                    }

                } catch (SQLException e) {
                    System.out.println("No connectat!");
                    e.printStackTrace();
                }
        } catch (PoolInitializationException e) {
            
        }
    }
}