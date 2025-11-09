package com.zugarez.zugarez_BACK.external.supabase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class SupabaseService {

    private final RestTemplate restTemplate;

    @Value("${supabase.url:}")
    private String propUrl;

    @Value("${supabase.key:}")
    private String propKey;

    @Value("${supabase.asistenciaTable:registro_asistencia}")
    private String asistenciaTableProp;

    @Value("${supabase.nominaTable:nomina_calculos}")
    private String nominaTableProp;

    public SupabaseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getUrl() {
        String fromEnv = System.getenv("SUPABASE_URL");
        return StringUtils.hasText(fromEnv) ? fromEnv : propUrl;
    }

    private String getKey() {
        String fromEnv = System.getenv("SUPABASE_KEY");
        if (StringUtils.hasText(fromEnv)) return fromEnv;
        String anon = System.getenv("SUPABASE_ANON_KEY");
        if (StringUtils.hasText(anon)) return anon;
        return propKey;
    }

    public String getAsistenciaTable() {
        String fromEnv = System.getenv("SUPABASE_ASISTENCIA_TABLE");
        return StringUtils.hasText(fromEnv) ? fromEnv : asistenciaTableProp;
    }

    public String getNominaTable() {
        String fromEnv = System.getenv("SUPABASE_NOMINA_TABLE");
        return StringUtils.hasText(fromEnv) ? fromEnv : nominaTableProp;
    }

    public boolean isConfigured() {
        return StringUtils.hasText(getUrl()) && StringUtils.hasText(getKey());
    }

    public ResponseEntity<String> insert(String table, Map<String, Object> row) {
        if (!isConfigured()) {
            log.warn("Supabase no configurado (SUPABASE_URL/SUPABASE_KEY), omitiendo insert a tabla {}", table);
            return new ResponseEntity<>("Supabase not configured", HttpStatus.NOT_IMPLEMENTED);
        }
        try {
            String endpoint = getUrl() + "/rest/v1/" + table;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", getKey());
            headers.setBearerAuth(getKey());
            headers.set("Prefer", "return=representation");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(row, headers);
            return restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error("Error insertando en Supabase (tabla {}): {}", table, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> upsert(String table, Map<String, Object> row, String onConflict) {
        if (!isConfigured()) {
            log.warn("Supabase no configurado, omitiendo upsert a tabla {}", table);
            return new ResponseEntity<>("Supabase not configured", HttpStatus.NOT_IMPLEMENTED);
        }
        try {
            String endpoint = getUrl() + "/rest/v1/" + table;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", getKey());
            headers.setBearerAuth(getKey());
            headers.set("Prefer", "resolution=merge-duplicates,return=representation");
            if (StringUtils.hasText(onConflict)) {
                headers.set("On-Conflict", onConflict);
            }
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(row, headers);
            return restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error("Error en upsert Supabase (tabla {}): {}", table, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
