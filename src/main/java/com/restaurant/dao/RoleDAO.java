package com.restaurant.dao;

import com.restaurant.model.Role;
import com.restaurant.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<Role> findAll() {

        List<Role> list = new ArrayList<>();

        String sql = "SELECT * FROM role";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Role r = new Role();
                r.setIdRole(rs.getInt("id_role"));
                r.setNomRole(rs.getString("nom_role"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
