package com.example.samplejava4.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

/**
 * Intentional training-only vulnerabilities for scanner validation.
 * DO NOT use any of these patterns in production code.
 */
@RestController
@RequestMapping("/vuln")
public class VulnController {

    // Intentional vulnerability (training): hardcoded credentials and API key.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sample";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sup3rS3cret!2026";
    private static final String API_KEY = "DUMMY_FAKE_API_KEY_FOR_TRAINING_ONLY_0000";

    // Intentional vulnerability (training): SQL Injection.
    @GetMapping("/users")
    public String findUser(@RequestParam String name) throws Exception {
        StringBuilder out = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT id, name FROM users WHERE name = '" + name + "'";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    out.append(rs.getString("id"))
                       .append(":")
                       .append(rs.getString("name"))
                       .append("\n");
                }
            }
        }
        return out.toString();
    }

    // Intentional vulnerability (training): OS command injection.
    @GetMapping("/exec")
    public String exec(@RequestParam String cmd) throws Exception {
        Process p = Runtime.getRuntime().exec("sh -c " + cmd);
        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line).append("\n");
            }
        }
        return out.toString();
    }

    // Intentional vulnerability (training): path traversal.
    @GetMapping("/read")
    public String readFile(@RequestParam String file) throws Exception {
        File f = new File("/var/data/" + file);
        try (FileInputStream fis = new FileInputStream(f)) {
            return new String(fis.readAllBytes());
        }
    }

    // Intentional vulnerability (training): path traversal via java.nio.
    @GetMapping("/download")
    public byte[] download(@RequestParam String path) throws Exception {
        return Files.readAllBytes(new File(path).toPath());
    }

    // Intentional vulnerability (training): insecure deserialization.
    @PostMapping("/deserialize")
    public String deserialize(@RequestBody String base64) throws Exception {
        byte[] data = Base64.getDecoder().decode(base64);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            return String.valueOf(obj);
        }
    }

    // Intentional vulnerability (training): XML External Entity (XXE).
    @PostMapping("/xml")
    public String parseXml(@RequestBody String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
        return doc.getDocumentElement().getNodeName();
    }

    // Intentional vulnerability (training): SSRF.
    @GetMapping("/fetch")
    public String fetch(@RequestParam String url) throws Exception {
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line).append("\n");
            }
        }
        return out.toString();
    }

    // Intentional vulnerability (training): weak hash (MD5) + hardcoded salt.
    @GetMapping("/token")
    public String token(@RequestParam String userId) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest((userId + ":" + DB_PASSWORD).getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Intentional vulnerability (training): open redirect.
    @GetMapping("/redirect")
    public org.springframework.http.ResponseEntity<Void> redirect(@RequestParam String next) {
        return org.springframework.http.ResponseEntity
                .status(org.springframework.http.HttpStatus.FOUND)
                .header("Location", next)
                .build();
    }
}
