package com.tanre.document_register.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class ReportDAO {

    private final NamedParameterJdbcTemplate jdbc;

    public ReportDAO(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private String loadSql(String path) throws IOException {
        return new String(
                Objects.requireNonNull(
                        this.getClass().getResourceAsStream(path)
                ).readAllBytes(),
                StandardCharsets.UTF_8
        );
    }


    public List<Map<String, Object>> getClaimDocumentReport(
            LocalDate startDate,
            LocalDate endDate
    ) throws IOException {

        String sql = loadSql("/sql/claimDocument.sql");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("startDate",
                        startDate != null
                                ? startDate.toString()
                                : null,
                        Types.VARCHAR)
                .addValue("endDate",
                        endDate != null
                                ? endDate.toString()
                                : null,
                        Types.VARCHAR);
        return jdbc.queryForList(sql, params);

    }

    public List<Map<String, Object>> getDebitNoteReport(
            LocalDate startDate,
            LocalDate endDate,
            String userName
    ) throws IOException {

        String sql = loadSql("/sql/debitNoteReport.sql");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userName", userName)
                .addValue("startDate",
                        startDate != null
                                ? startDate.toString()
                                : null,
                        Types.VARCHAR)
                .addValue("endDate",
                        endDate != null
                                ? endDate.toString()
                                : null,
                        Types.VARCHAR);
        return jdbc.queryForList(sql, params);

    }
}
