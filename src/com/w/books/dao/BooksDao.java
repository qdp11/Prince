package com.w.books.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.w.books.pojo.*;

public class BooksDao {
	
	
	private static final String DB_DEIVER = "org.postgresql.Driver";
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/";
	private static final String DB_NAME = "Books";
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "1";
	private static Connection connection = null;
	
	
	private static Gson gson = new Gson();
	
	
	private static void getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(DB_DEIVER);
		connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
	}
	
	private static void getClose(Connection connection, Statement st, PreparedStatement ps, ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		if (st != null) {
			st.close();
		}
		if (connection != null) {
			connection.close();
		}
	}
	
	
	public static String Query() throws ClassNotFoundException, SQLException {
		PreparedStatement ps = null;
		
		List<Book> booksList = new ArrayList<>();
		getConnection();
		
		String sql = "SELECT * FROM books;";
		System.out.println(sql);
		ps = connection.prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
			
			Book book = new Book();
			book.setBid(rs.getInt("Bid"));
			book.setBname(rs.getString("Bname"));
			book.setBnumber(rs.getInt("Bnumber"));
			
			booksList.add(book);
		}
		
		BooksBean booksBean = new BooksBean();
		
		booksBean.setRows(booksList);
		
		booksBean.setTotal(String.valueOf(booksList.size()));
		
		getClose(connection, null, ps, rs);
		return gson.toJson(booksBean);
	}

	
	public static String DELETE(String Bid) throws ClassNotFoundException, SQLException {
		Statement st = null;
		getConnection();
		
		st = connection.createStatement();
		String sql = "DELETE FROM books WHERE \"Bid\" in (" + Bid + ");";
		System.out.println(sql);
		
		st.executeUpdate(sql);
		
		getClose(connection, st, null, null);
		return "true";
	}

	public static String UPDATE(String json) throws ClassNotFoundException, SQLException {
		PreparedStatement ps = null;
		getConnection();
		
		
		Book book = gson.fromJson(json, Book.class);
		String sql = "UPDATE books SET \"Bname\" = ?,\"Bnumber\" = ? WHERE \"Bid\" = ?;";
		System.out.println(sql);
		
		ps = connection.prepareStatement(sql);
		ps.setString(1, book.getBname());
		ps.setInt(2, book.getBnumber());
		ps.setInt(3, book.getBid());
		ps.executeUpdate();
		
		getClose(connection, null, ps, null);
		return "true";
	}

	
	public static String INSERT(String json) throws ClassNotFoundException, SQLException {
		PreparedStatement ps = null;
		getConnection();

		
		Book book = gson.fromJson(json, Book.class);
		String sql = "INSERT INTO books (\"Bid\", \"Bname\", \"Bnumber\") VALUES (?, ?, ?);";
		 System.out.println(sql);
		 
		ps = connection.prepareStatement(sql);
		ps.setInt(1, book.getBid());
		ps.setString(2, book.getBname());
		ps.setInt(3, book.getBnumber());
		ps.executeUpdate();
		
		getClose(connection, null, ps, null);
		return "true";
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		System.out.println(Query());
		

	}
}
