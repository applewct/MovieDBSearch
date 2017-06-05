/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviesearch;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author wtai
 */
public class HW3 extends javax.swing.JFrame {

    ArrayList<JCheckBox> genreCheckBoxs = new ArrayList<JCheckBox>();
    ArrayList<JCheckBox> countryCheckBoxs = new ArrayList<JCheckBox>();
    ArrayList<JCheckBox> tagCheckBoxs = new ArrayList<JCheckBox>();
    ArrayList<JCheckBox> movieCheckBoxs = new ArrayList<JCheckBox>();

    /**
     * Creates new form NewJFrame
     */
    public HW3() {
        initComponents();
        // get genre from DB
        Connection con = null;
        ResultSet result = null;
    
        try {
            con = openConnection();

            result = searchAllTuples(con, "moviegenres");
            ResultSetMetaData meta = result.getMetaData();
            Set<String> genreSet = new HashSet<String>();
            while (result.next()) {
                genreSet.add(result.getString(2));
            }
            
            gnerepanel.setLayout(new GridLayout(0,1));
            Iterator<String> it = genreSet.iterator();
            ActionListener actL = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filterGenreNYear();
                }
            };
            while (it.hasNext()){
                JCheckBox cb = new JCheckBox (it.next());
                cb.addActionListener(actL);
                genreCheckBoxs.add(cb);
                gnerepanel.add(cb); 
            }
            gnerepanel.revalidate();
            gnerepanel.repaint();
        } catch (SQLException e) {
            System.err.println("Errors occurs when communicating with the database server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot find the database driver");
        } finally {
            // Never forget to close database connection
            closeConnection(con);
        }
    }
    
    private void filterGenreNYear(){
        Set<String> selectedGenre = new HashSet<String>();
        for (JCheckBox cb : genreCheckBoxs){
            if (cb.isSelected()) {
                selectedGenre.add(cb.getText());
                System.out.println(cb.getText());
            }
        }
        
        String start= startYear.getText();
        String end = endYear.getText();
        
        Connection con = null;
        ResultSet result = null;
    
        try {
            con = openConnection();
            Statement stmt = con.createStatement();
            String query = "";
            if (andorselector.getSelectedItem().toString().equals("OR")){
                query += "SELECT mc.country FROM moviecountries mc, movies m, moviegenres mg WHERE mc.movieid = m.id AND m.id = mg.movieid";
                query += " AND (";
                for (String genre : selectedGenre){
                    query += " mg.genre = \'" + genre + "\'";
                    query += " " + andorselector.getSelectedItem().toString();
                }
                query = query.substring(0, query.length() - andorselector.getSelectedItem().toString().length());
                query += ")";
                query += " AND m.year <= " + end;
                query += " AND m.year >= " + start;
                System.out.println(query);
            }
            else{
                query += "SELECT mc.country FROM moviecountries mc, movies m, moviegenres mg WHERE mc.movieid = m.id AND m.id = mg.movieid";
                query += " AND m.year <= " + end;
                query += " AND m.year >= " + start;
                for (String genre : selectedGenre){
                    query += " AND mc.movieid IN (SELECT mg.movieid FROM moviegenres mg WHERE mg.genre = \'" + genre + "\')";
                }
            }
            System.out.println(query);
            result = stmt.executeQuery(query);
            Set<String> countrySet = new HashSet<String>();
            while (result.next()) {
                countrySet.add(result.getString(1));
            }
            countrypanel.removeAll();
            countrypanel.setLayout(new GridLayout(0,1));
            Iterator<String> it = countrySet.iterator();
            ActionListener countryActl = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filterGenreNYearNCountry();
                }
            };
            while (it.hasNext()){
                JCheckBox cb = new JCheckBox (it.next());
                cb.addActionListener(countryActl);
                countrypanel.add(cb); 
                countryCheckBoxs.add(cb);
            }
            countrypanel.revalidate();
            countrypanel.repaint();
        } catch (SQLException e) {
            System.err.println("Errors occurs when communicating with the database server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot find the database driver");
        } finally {
            // Never forget to close database connection
            closeConnection(con);
        }
    }

private void filterGenreNYearNCountry(){
        Set<String> selectedGenre = new HashSet<String>();
        for (JCheckBox cb : genreCheckBoxs){
            if (cb.isSelected()) {
                selectedGenre.add(cb.getText());
                System.out.println(cb.getText());
            }
        }
        
        Set<String> selectedCountry = new HashSet<String>();
        for (JCheckBox cb : countryCheckBoxs){
            if (cb.isSelected()) {
                selectedCountry.add(cb.getText());
                System.out.println(cb.getText());
            }
        }
        
        String start= startYear.getText();
        String end = endYear.getText();
        
        Connection con = null;
        ResultSet result = null;
    
        try {
            con = openConnection();
            Statement stmt = con.createStatement();
            String query = "";
            
            if (andorselector.getSelectedItem().toString().equals("OR")){
                query += "SELECT mt.tagid, t.value FROM tags t, movietags mt, moviecountries mc, movies m, moviegenres mg WHERE t.id = mt.tagid AND mt.movieid = m.id AND mc.movieid = m.id AND m.id = mg.movieid";
                query += " AND (";

                for (String genre : selectedGenre){
                    query += " mg.genre = \'" + genre + "\'";
                    query += " " + andorselector.getSelectedItem().toString();
                }
                query = query.substring(0, query.length() - andorselector.getSelectedItem().toString().length());
                query += ")";

                query += " AND (";

                for (String country : selectedCountry){
                    query += " mc.country = \'" + country + "\'";
                    query += " " + andorselector.getSelectedItem().toString();
                }
                query = query.substring(0, query.length() - andorselector.getSelectedItem().toString().length());
                query += ")";

                query += " AND m.year <= " + end;
                query += " AND m.year >= " + start;
            }
            else {
                query += "SELECT t.id, t.value FROM tags t, movietags mt, moviecountries mc, movies m, moviegenres mg WHERE t.id = mt.tagid AND mt.movieid = m.id AND mc.movieid = m.id AND m.id = mg.movieid";
                query += " AND m.year <= " + end;
                query += " AND m.year >= " + start;
                for (String genre : selectedGenre){
                    query += " AND mt.movieid IN (SELECT mg.movieid FROM moviegenres mg WHERE mg.genre = \'" + genre + "\')";
                }
                for (String country : selectedCountry){
                    query += " AND mt.movieid IN (SELECT mc.movieid FROM moviecountries mc WHERE mc.country = \'" + country + "\')";
                }
                
            }
            
            System.out.println(query);
            result = stmt.executeQuery(query);
            Set<String> tagSet = new HashSet<String>();
            while (result.next()) {
                tagSet.add(result.getString(1) + "\t" + result.getString(2));
            }
            tagpanel.removeAll();
            tagpanel.setLayout(new GridLayout(0,1));
            Iterator<String> it = tagSet.iterator();
            ActionListener tagActl = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                }
            };
            while (it.hasNext()){
                JCheckBox cb = new JCheckBox (it.next());
                cb.addActionListener(tagActl);
                tagpanel.add(cb); 
                tagCheckBoxs.add(cb);
            }
            tagpanel.revalidate();
            tagpanel.repaint();
        } catch (SQLException e) {
            System.err.println("Errors occurs when communicating with the database server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot find the database driver");
        } finally {
            // Never forget to close database connection
            closeConnection(con);
        }
}

private void filterGenreNYearNCountryNActor(){
        Set<String> selectedGenre = new HashSet<String>();
        for (JCheckBox cb : genreCheckBoxs){
            if (cb.isSelected()) {
                selectedGenre.add(cb.getText());
                System.out.println(cb.getText());
            }
        }
        
        Set<String> selectedCountry = new HashSet<String>();
        for (JCheckBox cb : countryCheckBoxs){
            if (cb.isSelected()) {
                selectedCountry.add(cb.getText());
                System.out.println(cb.getText());
            }
        }
        
        String start= startYear.getText();
        String end = endYear.getText();
        
        Connection con = null;
        ResultSet result = null;
    
        try {
            con = openConnection();
            Statement stmt = con.createStatement();
            String query = "";
            
            if (andorselector.getSelectedItem().toString().equals("OR")){
                query += "SELECT t.id, t.value FROM movieactors ma, moviedirectors md, tags t, movietags mt, moviecountries mc, movies m, moviegenres mg WHERE m.id = ma.movieid AND m.id = md.movieid ANDt.id = mt.tagid AND mt.movieid = m.id AND mc.movieid = m.id AND m.id = mg.movieid";
                query += " AND (";

                for (String genre : selectedGenre){
                    query += " mg.genre = \'" + genre + "\'";
                    query += " " + andorselector.getSelectedItem().toString();
                }
                query = query.substring(0, query.length() - andorselector.getSelectedItem().toString().length());
                query += ")";

                query += " AND (";

                for (String country : selectedCountry){
                    query += " mc.country = \'" + country + "\'";
                    query += " " + andorselector.getSelectedItem().toString();
                }
                query = query.substring(0, query.length() - andorselector.getSelectedItem().toString().length());
                query += ")";

                query += " AND m.year <= " + end;
                query += " AND m.year >= " + start;
            }
            else {
                query += "SELECT t.id, t.value FROM movieactors ma, moviedirectors md, tags t, movietags mt, moviecountries mc, movies m, moviegenres mg WHERE m.id = ma.movieid AND m.id = md.movieid AND t.id = mt.tagid AND mt.movieid = m.id AND mc.movieid = m.id AND m.id = mg.movieid";
                query += " AND m.year <= " + end;
                query += " AND m.year >= " + start;
                for (String genre : selectedGenre){
                    query += " AND mt.movieid IN (SELECT mg.movieid FROM moviegenres mg WHERE mg.genre = \'" + genre + "\')";
                }
                for (String country : selectedCountry){
                    query += " AND mt.movieid IN (SELECT mc.movieid FROM moviecountries mc WHERE mc.country = \'" + country + "\')";
                }
                
            }
            if (!actorOne.getText().equals("actor1")){
                query += " AND m.id IN (SELECT ma.movieid FROM movieactors ma WHERE ma.actorname LIKE \'" + actorOne.getText() + "\')";
            }
            if (!actorTwo.getText().equals("actor2")){
                query += " AND m.id IN (SELECT ma.movieid FROM movieactors ma WHERE ma.actorname LIKE \'%" + actorTwo.getText() + "%\')";
            }
            if (!actorThree.getText().equals("actor3")){
                query += " AND m.id IN (SELECT ma.movieid FROM movieactors ma WHERE ma.actorname LIKE \'%" + actorThree.getText() + "%\')";
            }
            if (!directorOne.getText().equals("director1")){
                query += " AND m.id IN (SELECT md.movieid FROM moviedirectors md WHERE md.directorname LIKE \'%" + directorOne.getText() + "%\')";
            }
            
            System.out.println(query);
            result = stmt.executeQuery(query);
            Set<String> tagSet = new HashSet<String>();
            while (result.next()) {
                tagSet.add(result.getString(1) + "\t" + result.getString(2));
            }
            tagpanel.removeAll();
            tagpanel.setLayout(new GridLayout(0,1));
            Iterator<String> it = tagSet.iterator();
            ActionListener tagActl = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                }
            };
            while (it.hasNext()){
                JCheckBox cb = new JCheckBox (it.next());
                cb.addActionListener(tagActl);
                tagpanel.add(cb); 
                tagCheckBoxs.add(cb);
            }
            tagpanel.revalidate();
            tagpanel.repaint();
        } catch (SQLException e) {
            System.err.println("Errors occurs when communicating with the database server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot find the database driver");
        } finally {
            // Never forget to close database connection
            closeConnection(con);
        }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        gnerepanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        startYear = new javax.swing.JTextField();
        endYear = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        countrypanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        andorselector = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        actorOne = new javax.swing.JTextField();
        actorTwo = new javax.swing.JTextField();
        actorThree = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tagpanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        moviepanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        directorOne = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tagCompare = new javax.swing.JComboBox<>();
        tagValue = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        queryBox = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        runSearch = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        userSearch = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        userResult = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout gnerepanelLayout = new javax.swing.GroupLayout(gnerepanel);
        gnerepanel.setLayout(gnerepanelLayout);
        gnerepanelLayout.setHorizontalGroup(
            gnerepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 126, Short.MAX_VALUE)
        );
        gnerepanelLayout.setVerticalGroup(
            gnerepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 695, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(gnerepanel);

        jLabel1.setText("Genres");

        startYear.setText("0");
        startYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startYearActionPerformed(evt);
            }
        });

        endYear.setText("3000");
        endYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endYearActionPerformed(evt);
            }
        });

        jLabel2.setText("From");

        jLabel3.setText("To");

        javax.swing.GroupLayout countrypanelLayout = new javax.swing.GroupLayout(countrypanel);
        countrypanel.setLayout(countrypanelLayout);
        countrypanelLayout.setHorizontalGroup(
            countrypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 126, Short.MAX_VALUE)
        );
        countrypanelLayout.setVerticalGroup(
            countrypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 695, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(countrypanel);

        jLabel4.setText("Country");

        andorselector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));

        jLabel5.setText("AND or OR");

        jLabel6.setText("Cast");

        actorOne.setText("actor1");
        actorOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actorOneActionPerformed(evt);
            }
        });

        actorTwo.setText("actor2");

        actorThree.setText("actor3");

        javax.swing.GroupLayout tagpanelLayout = new javax.swing.GroupLayout(tagpanel);
        tagpanel.setLayout(tagpanelLayout);
        tagpanelLayout.setHorizontalGroup(
            tagpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );
        tagpanelLayout.setVerticalGroup(
            tagpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(tagpanel);

        javax.swing.GroupLayout moviepanelLayout = new javax.swing.GroupLayout(moviepanel);
        moviepanel.setLayout(moviepanelLayout);
        moviepanelLayout.setHorizontalGroup(
            moviepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );
        moviepanelLayout.setVerticalGroup(
            moviepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
        );

        jScrollPane4.setViewportView(moviepanel);

        jLabel7.setText("Director");

        directorOne.setText("director1");
        directorOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directorOneActionPerformed(evt);
            }
        });

        jLabel9.setText("Tag Weight");

        tagCompare.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=", "<", ">" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(actorTwo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(actorOne, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(actorThree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(directorOne, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tagCompare, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tagValue, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)))
                        .addGap(40, 40, 40)))
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(actorOne, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(actorTwo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(actorThree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(directorOne, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tagCompare, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tagValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        queryBox.setText("Query");
        queryBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryBoxActionPerformed(evt);
            }
        });
        jScrollPane5.setViewportView(queryBox);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1090, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 73, Short.MAX_VALUE))
        );

        jLabel8.setText("Tags");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 162, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 87, Short.MAX_VALUE)
        );

        jLabel10.setText("Movies");

        runSearch.setText("SEARCH");
        runSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );

        jLabel11.setText("User Result");

        userSearch.setText("User Search");
        userSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout userResultLayout = new javax.swing.GroupLayout(userResult);
        userResult.setLayout(userResultLayout);
        userResultLayout.setHorizontalGroup(
            userResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 358, Short.MAX_VALUE)
        );
        userResultLayout.setVerticalGroup(
            userResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane6.setViewportView(userResult);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel1)
                        .addGap(91, 91, 91)
                        .addComponent(jLabel4)
                        .addGap(99, 99, 99)
                        .addComponent(jLabel6)
                        .addGap(56, 56, 56)
                        .addComponent(jLabel8)
                        .addGap(227, 227, 227)
                        .addComponent(jLabel10)
                        .addGap(403, 403, 403)
                        .addComponent(jLabel11))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(18, 18, 18))
                                    .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel2)
                                        .addGap(2, 2, 2)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(startYear)
                                    .addComponent(endYear)))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(andorselector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(93, 93, 93)
                                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 0, Short.MAX_VALUE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(runSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userSearch)
                        .addGap(55, 55, 55)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(810, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(startYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(endYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(andorselector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(runSearch)
                            .addComponent(userSearch))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startYearActionPerformed
        // TODO add your handling code here:
        filterGenreNYear();
    }//GEN-LAST:event_startYearActionPerformed

    private void endYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endYearActionPerformed
        // TODO add your handling code here:
        filterGenreNYear();
    }//GEN-LAST:event_endYearActionPerformed

    private void runSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runSearchActionPerformed
        // TODO add your handling code here:
        Set<String> selectedGenre = new HashSet<String>();
        for (JCheckBox cb : genreCheckBoxs){
            if (cb.isSelected()) {
                selectedGenre.add(cb.getText());
                System.out.println(cb.getText());
            }
        }
        
        Set<String> selectedCountry = new HashSet<String>();
        for (JCheckBox cb : countryCheckBoxs){
            if (cb.isSelected()) {
                selectedCountry.add(cb.getText());
                System.out.println(cb.getText());
            }
        }
        
        Set<String> selectedTag = new HashSet<String>();
        for (JCheckBox cb : tagCheckBoxs){
            if (cb.isSelected()) {
                selectedTag.add(cb.getText().split("\t")[0]);
                System.out.println(cb.getText());
            }
        }
        
        String start= startYear.getText();
        String end = endYear.getText();
        
        Connection con = null;
        ResultSet result = null;
    
        try {
            con = openConnection();
            Statement stmt = con.createStatement();
            String query = "SELECT m.id, m.title, mg.genre, m.RTAUDIENCERATING, m.RTAUDIENCENUMRATINGS FROM movies m, moviegenres mg";
            
            if (!tagCheckBoxs.isEmpty()){
                query += ", tags t, movietags mt";
            }
            
            if (!actorOne.getText().equals("actor1") || !actorTwo.getText().equals("actor2") || !actorThree.getText().equals("actor3")){
                query += ", movieactors ma";
            }
            
            if (!directorOne.getText().equals("director1")){
                query += ", moviedirectors md";
            }
            
            if (!countryCheckBoxs.isEmpty()){
                query += ", moviecountries mc ";
            }
            query += "WHERE m.id = mg.movieid";
            
            if (!tagCheckBoxs.isEmpty()){
                query += " AND t.id = mt.tagid AND mt.movieid = m.id";
            }
            
            if (!actorOne.getText().equals("actor1") || !actorTwo.getText().equals("actor2") || !actorThree.getText().equals("actor3")){
                query += " AND m.id = ma.movieid";
            }
            
            if (!directorOne.getText().equals("director1")){
                query += " AND m.id = md.movieid";
            }
            
            if (!countryCheckBoxs.isEmpty()){
                query += " AND mc.movieid = m.id";
            }
            
            if (andorselector.getSelectedItem().toString().equals("OR")){

                // Genre Part
                query += " AND (";
                for (String genre : selectedGenre){
                    query += " mg.genre = \'" + genre + "\'";
                    query += " " + andorselector.getSelectedItem().toString();
                }
                query = query.substring(0, query.length() - andorselector.getSelectedItem().toString().length());
                query += ")";



                // Country Part
                query += " AND (";
                for (String country : selectedCountry){
                    query += " mc.country = \'" + country + "\'";
                    query += " " + andorselector.getSelectedItem().toString();
                }
                query = query.substring(0, query.length() - andorselector.getSelectedItem().toString().length());
                query += ")";

                // Tag Part
                query += " AND (";
                for (String tag : selectedTag){
                    query += " t.id = \'" + tag + "\'";
                    query += " " + andorselector.getSelectedItem().toString();
                }
                query = query.substring(0, query.length() - andorselector.getSelectedItem().toString().length());
                query += ")";


                query += " AND m.year <= " + end;
                query += " AND m.year >= " + start;
            }
            else{
                query += " AND m.year <= " + end;
                query += " AND m.year >= " + start;
                for (String genre : selectedGenre){
                    query += " AND mt.movieid IN (SELECT mg.movieid FROM moviegenres mg WHERE mg.genre = \'" + genre + "\')";
                }
                for (String country : selectedCountry){
                    query += " AND mt.movieid IN (SELECT mc.movieid FROM moviecountries mc WHERE mc.country = \'" + country + "\')";
                }
                for (String tag : selectedTag){
                    query += " AND mt.movieid IN (SELECT mt.movieid FROM movietags mt WHERE mt.tagid = \'" + tag + "\')";
                }
            }
            if (!actorOne.getText().equals("actor1")){
                query += " AND m.id IN (SELECT ma.movieid FROM movieactors ma WHERE ma.actorname LIKE \'%" + actorOne.getText() + "%\')";
            }
            if (!actorTwo.getText().equals("actor2")){
                query += " AND m.id IN (SELECT ma.movieid FROM movieactors ma WHERE ma.actorname LIKE \'%" + actorTwo.getText() + "%\')";
            }
            if (!actorThree.getText().equals("actor3")){
                query += " AND m.id IN (SELECT ma.movieid FROM movieactors ma WHERE ma.actorname LIKE \'%" + actorThree.getText() + "%\')";
            }
            if (!directorOne.getText().equals("director1")){
                query += " AND m.id IN (SELECT md.movieid FROM moviedirectors md WHERE md.directorname LIKE \'%" + directorOne.getText() + "%\')";
            }
            if (!tagValue.getText().equals("")){
                query += " AND mt.tagweight " + tagCompare.getSelectedItem().toString() + " " + tagValue.getText();
            }
            System.out.println(query);
            queryBox.setText(query);
            result = stmt.executeQuery(query);
            
            // paint movie
            Set<String> movieSet = new HashSet<String>();
            while (result.next()) {
                movieSet.add(result.getString(1) + "\t" + result.getString(2) + "\t" + result.getString(3) + "\t" + result.getString(4) + "\t" + result.getString(5));
            }
            moviepanel.removeAll();
            moviepanel.setLayout(new GridLayout(0,1));
            Iterator<String> it = movieSet.iterator();
            ActionListener movieActl = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                }
            };
            while (it.hasNext()){
                JCheckBox cb = new JCheckBox (it.next());
                cb.addActionListener(movieActl);
                moviepanel.add(cb); 
                movieCheckBoxs.add(cb);
            }
            moviepanel.revalidate();
            moviepanel.repaint();
            
            
        } catch (SQLException e) {
            System.err.println("Errors occurs when communicating with the database server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot find the database driver");
        } finally {
            // Never forget to close database connection
            closeConnection(con);
        }
        
    }//GEN-LAST:event_runSearchActionPerformed

    private void actorOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actorOneActionPerformed
        // TODO add your handling code here:
        filterGenreNYearNCountryNActor();
    }//GEN-LAST:event_actorOneActionPerformed

    private void directorOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directorOneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_directorOneActionPerformed

    private void queryBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_queryBoxActionPerformed

    private void userSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userSearchActionPerformed
        // TODO add your handling code here:
        Set<String> selectedMovie = new HashSet<String>();
        for (JCheckBox cb : movieCheckBoxs){
            if (cb.isSelected()) {
                selectedMovie.add(cb.getText().split("\t")[0]);
                System.out.println(cb.getText());
            }
        }
        Set<String> selectedtag = new HashSet<String>();
        for (JCheckBox cb : tagCheckBoxs){
            if (cb.isSelected()) {
                selectedtag.add(cb.getText().split("\t")[0]);
                System.out.println(cb.getText());
            }
        }
        Connection con = null;
        ResultSet result = null;
    
        try {
            con = openConnection();
            Statement stmt = con.createStatement();
            String query = "SELECT ut.userid, mt.movieid, t.value FROM usertags ut, movietags mt, tags t WHERE t.id = mt.tagid AND t.id = ut.tagid AND mt.movieid = ";
            
            for (String movie : selectedMovie){
                    query += "\'" + movie + "\'";
            }    
            
                
            for (String tag : selectedtag){
                    query += " AND t.id = \'" + tag + "\'";
            }
            
            System.out.println(query);
            queryBox.setText(query);
            result = stmt.executeQuery(query);
            
            // paint movie
            Set<String> tagSet = new HashSet<String>();
            while (result.next()) {
                tagSet.add("User " + result.getString(1) + "\ttags movie " + result.getString(2) + "\twith tag " + result.getString(3));
            }
            userResult.removeAll();
            userResult.setLayout(new GridLayout(0,1));
            Iterator<String> it = tagSet.iterator();
            ActionListener movieActl = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                }
            };
            while (it.hasNext()){
                JCheckBox cb = new JCheckBox (it.next());
                cb.addActionListener(movieActl);
                userResult.add(cb); 
            }
            userResult.revalidate();
            userResult.repaint();
            
            
        } catch (SQLException e) {
            System.err.println("Errors occurs when communicating with the database server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot find the database driver");
        } finally {
            // Never forget to close database connection
            closeConnection(con);
        }
    }//GEN-LAST:event_userSearchActionPerformed

    public static Connection openConnection() throws SQLException, ClassNotFoundException {
        // Load the Oracle database driver
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

        /*
        Here is the information needed when connecting to a database
        server. These values are now hard-coded in the program. In
        general, they should be stored in some configuration file and
        read at run time.
        */
        String host = "localhost";
        String port = "1521";
        String dbName = "orcl12c";
        String userName = "sys as sysdba";
        String password = "oracle";

        // Construct the JDBC URL
        String dbURL = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;
        return DriverManager.getConnection(dbURL, userName, password);
    }
    
    public static void closeConnection(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            System.err.println("Cannot close connection: " + e.getMessage());
        }
    }
    
    public static ResultSet searchAllTuples(Connection con, String table) throws SQLException {
        Statement stmt = con.createStatement();
        return stmt.executeQuery("SELECT * FROM " + table);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HW3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HW3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HW3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HW3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HW3().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField actorOne;
    private javax.swing.JTextField actorThree;
    private javax.swing.JTextField actorTwo;
    private javax.swing.JComboBox<String> andorselector;
    private javax.swing.JPanel countrypanel;
    private javax.swing.JTextField directorOne;
    private javax.swing.JTextField endYear;
    private javax.swing.JPanel gnerepanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPanel moviepanel;
    private javax.swing.JTextField queryBox;
    private javax.swing.JButton runSearch;
    private javax.swing.JTextField startYear;
    private javax.swing.JComboBox<String> tagCompare;
    private javax.swing.JTextField tagValue;
    private javax.swing.JPanel tagpanel;
    private javax.swing.JPanel userResult;
    private javax.swing.JButton userSearch;
    // End of variables declaration//GEN-END:variables
}
