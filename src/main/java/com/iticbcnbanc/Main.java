package com.iticbcnbanc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;
 
 
public class Main {
    public static void main(String[] args) {
        Properties prop = new Properties();

        try {
            prop.load(Files.newBufferedReader(Path.of("banc","src","main","resources","conn.properties")));
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
              
                for (String resultat : readAllStatements(cnn)) {
                System.out.println("moviment amb id: " + resultat);
            }

            System.out.println("  ");
               System.out.println("Moviments amb import >= 5:");

                for (String resultat : readStatementsByAmount(cnn, 5.0)) {
                    System.out.println(resultat);
                }

         } catch (SQLException e) {
                    System.out.println("No connectat!");
                    e.printStackTrace();
            }

        } catch (PoolInitializationException e) {
            System.out.println("No s'ha pogut connectar a la base de dades: " + e.getMessage());
            
        }

    }

    public static List<String> readAllStatements(Connection conn) throws SQLException {

        List<String> resultatQuery = new ArrayList<String>();

        String query = "SELECT * FROM statements";

        PreparedStatement ps = conn.prepareStatement(query);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String resultat = String.join(";",
                        rs.getString("id_statement"),
                        rs.getString("tipus_moviment"),
                        rs.getString("import"),
                        rs.getString("divisa")
                );

                resultatQuery.add(resultat);
            }
        }

        return resultatQuery;
    }

    public static List<String> readStatementsByAmount(Connection conn, double quantitat) throws SQLException {
        List<String> resultatQuery = new ArrayList<String>();
        String query = "SELECT * FROM statements WHERE import >= ?";

        PreparedStatement ps = conn.prepareStatement(query);

        ps.setDouble(1, quantitat);

            try (ResultSet rs = ps.executeQuery()) {

                ResultSetMetaData meta = rs.getMetaData();
                String header = String.join(";",
                    meta.getColumnName(1),
                    meta.getColumnName(3),
                    meta.getColumnName(8),
                    meta.getColumnName(10)
            );
            resultatQuery.add(header);

            while (rs.next()) {
                String resultat = String.join(";",
                    rs.getString(1),
                    rs.getString(3),
                    rs.getString(8),
                    rs.getString(10)
                );
                resultatQuery.add(resultat);
            }
        }
        return resultatQuery;
    }
}