package com.eleven.logistics.hub.infrastructure.config;

import com.eleven.logistics.hub.infrastructure.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GeocodingService geocodingService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // p_hub 초기 데이터
        String[][] hubData = {
                {"서울특별시 센터", "서울특별시 송파구 송파대로 55"},
                {"경기 북부 센터", "경기도 고양시 덕양구 권율대로 570"},
                {"경기 남부 센터", "경기도 이천시 덕평로 257-21"},
                {"부산광역시 센터", "부산 동구 중앙대로 206"},
                {"대구광역시 센터", "대구 북구 태평로 161"},
                {"인천광역시 센터", "인천 남동구 정각로 29"},
                {"광주광역시 센터", "광주 서구 내방로 111"},
                {"대전광역시 센터", "대전 서구 둔산로 100"},
                {"울산광역시 센터", "울산 남구 중앙로 201"},
                {"세종특별자치시 센터", "세종특별자치시 한누리대로 2130"},
                {"강원특별자치도 센터", "강원특별자치도 춘천시 중앙로 1"},
                {"충청북도 센터", "충북 청주시 상당구 상당로 82"},
                {"충청남도 센터", "충남 홍성군 홍북읍 충남대로 21"},
                {"전북특별자치도 센터", "전북특별자치도 전주시 완산구 효자로 225"},
                {"전라남도 센터", "전남 무안군 삼향읍 오룡길 1"},
                {"경상북도 센터", "경북 안동시 풍천면 도청대로 455"},
                {"경상남도 센터", "경남 창원시 의창구 중앙대로 300"}
        };

        // hubId를 저장할 맵
        Map<String, UUID> hubIdMap = new HashMap<>();

        // p_hub 데이터 삽입
        for (String[] hub : hubData) {
            String name = hub[0];
            String address = hub[1];
            double[] coordinates = geocodingService.getCoordinates(address);

            if (coordinates != null && coordinates.length == 2) {
                double latitude = coordinates[0];
                double longitude = coordinates[1];
                String createdBy = "MASTER";

                String insertSql = "INSERT INTO p_hub (id, name, address, latitude, longitude, created_at, created_by) " +
                        "SELECT uuid_generate_v4(), ?, ?, ?, ?, now(), ? " +
                        "WHERE NOT EXISTS (SELECT 1 FROM p_hub WHERE name = ?) " +
                        "RETURNING id";
                UUID hubId = jdbcTemplate.queryForObject(insertSql, UUID.class, name, address, latitude, longitude, createdBy, name);
                hubIdMap.put(name, hubId);
            }
        }

        // p_company 초기 데이터
        String[][] companyData = {
                {"육류 생산 업체", "서울특별시 송파구 송파대로 100", "PRODUCER_COMPANY", "producer1", "서울특별시 센터"},
                {"가공 식품 공장", "경기도 고양시 덕양구 권율대로 600", "PRODUCER_COMPANY", "producer2", "경기 북부 센터"},
                {"수산물 수령 업체", "부산 동구 중앙대로 250", "RECEIVER_COMPANY", "receiver1", "부산광역시 센터"},
                {"농산물 생산 협동조합", "경기도 이천시 덕평로 300", "PRODUCER_COMPANY", "producer3", "경기 남부 센터"},
                {"도매 유통 업체", "대구 북구 태평로 200", "RECEIVER_COMPANY", "receiver2", "대구광역시 센터"},
                {"유제품 생산 공장", "인천 남동구 정각로 50", "PRODUCER_COMPANY", "producer4", "인천광역시 센터"},
                {"물류 수령 센터", "광주 서구 내방로 150", "RECEIVER_COMPANY", "receiver3", "광주광역시 센터"},
                {"곡물 가공 업체", "대전 서구 둔산로 120", "PRODUCER_COMPANY", "producer5", "대전광역시 센터"},
                {"식자재 유통 업체", "울산 남구 중앙로 220", "RECEIVER_COMPANY", "receiver4", "울산광역시 센터"},
                {"과일 생산 농장", "세종특별자치시 한누리대로 2150", "PRODUCER_COMPANY", "producer6", "세종특별자치시 센터"},
                {"소매 수령 업체", "강원특별자치도 춘천시 중앙로 50", "RECEIVER_COMPANY", "receiver5", "강원특별자치도 센터"},
                {"야채 가공 공장", "충북 청주시 상당구 상당로 100", "PRODUCER_COMPANY", "producer7", "충청북도 센터"},
                {"식품 수령 창고", "충남 홍성군 홍북읍 충남대로 50", "RECEIVER_COMPANY", "receiver6", "충청남도 센터"},
                {"수산물 가공 업체", "전북특별자치도 전주시 완산구 효자로 250", "PRODUCER_COMPANY", "producer8", "전북특별자치도 센터"},
                {"도소매 유통 업체", "전남 무안군 삼향읍 오룡길 20", "RECEIVER_COMPANY", "receiver7", "전라남도 센터"},
                {"축산물 생산 공장", "경북 안동시 풍천면 도청대로 500", "PRODUCER_COMPANY", "producer9", "경상북도 센터"},
                {"물류 수령 업체", "경남 창원시 의창구 중앙대로 350", "RECEIVER_COMPANY", "receiver8", "경상남도 센터"},
                {"과자 제조 업체", "서울특별시 송파구 송파대로 80", "PRODUCER_COMPANY", "producer10", "서울특별시 센터"},
                {"마트 수령 센터", "경기도 고양시 덕양구 권율대로 580", "RECEIVER_COMPANY", "receiver9", "경기 북부 센터"},
                {"음료 생산 공장", "부산 동구 중앙대로 230", "PRODUCER_COMPANY", "producer11", "부산광역시 센터"},
                {"창고 수령 업체", "경기도 이천시 덕평로 280", "RECEIVER_COMPANY", "receiver10", "경기 남부 센터"},
                {"빵 생산 업체", "대구 북구 태평로 180", "PRODUCER_COMPANY", "producer12", "대구광역시 센터"},
                {"슈퍼마켓 수령", "인천 남동구 정각로 40", "RECEIVER_COMPANY", "receiver11", "인천광역시 센터"},
                {"냉동식품 공장", "광주 서구 내방로 130", "PRODUCER_COMPANY", "producer13", "광주광역시 센터"},
                {"식당 수령 업체", "대전 서구 둔산로 110", "RECEIVER_COMPANY", "receiver12", "대전광역시 센터"},
                {"김치 제조 업체", "울산 남구 중앙로 210", "PRODUCER_COMPANY", "producer14", "울산광역시 센터"},
                {"도매 수령 센터", "세종특별자치시 한누리대로 2140", "RECEIVER_COMPANY", "receiver13", "세종특별자치시 센터"},
                {"간식 생산 공장", "강원특별자치도 춘천시 중앙로 30", "PRODUCER_COMPANY", "producer15", "강원특별자치도 센터"},
                {"유통 수령 업체", "충북 청주시 상당구 상당로 90", "RECEIVER_COMPANY", "receiver14", "충청북도 센터"},
                {"해산물 가공 업체", "충남 홍성군 홍북읍 충남대로 30", "PRODUCER_COMPANY", "producer16", "충청남도 센터"}
        };

        // p_company 데이터 삽입
        for (String[] company : companyData) {
            String name = company[0];
            String address = company[1];
            String type = company[2];
            String username = company[3];
            String hubName = company[4];
            UUID hubId = hubIdMap.get(hubName);
            String createdBy = "MASTER";

            if (hubId != null) {
                String insertSql = "INSERT INTO p_company (id, hub_id, name, address, type, username,created_at, created_by) " +
                        "SELECT uuid_generate_v4(), ?, ?, ?, ?, ?,now(),? " +
                        "WHERE NOT EXISTS (SELECT 1 FROM p_company WHERE name = ?)";
                jdbcTemplate.update(insertSql, hubId, name, address, type, username,createdBy,name);
            }
        }
    }
}