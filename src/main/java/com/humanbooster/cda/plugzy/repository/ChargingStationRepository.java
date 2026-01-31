package com.humanbooster.cda.plugzy.repository;

import com.humanbooster.cda.plugzy.entity.ChargingStation;
import com.humanbooster.cda.plugzy.entity.ChargingStationGroup;
import com.humanbooster.cda.plugzy.repository.projection.StationListItemProjection;
import com.humanbooster.cda.plugzy.repository.projection.StationMapMarkerProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, UUID> {
    @Query("""
        select cs
        from ChargingStation cs
        join fetch cs.stationGroup g
        join fetch g.location l
        where cs.isActive = true
    """)
    List<ChargingStation> findAllActiveWithLocation();

    @Query("""
        select cs
        from ChargingStation cs
        join fetch cs.stationGroup g
        join fetch g.location l
        where cs.id = :id
    """)
    Optional<ChargingStation> findOneWithDetails(@Param("id") UUID id);

    @Query(value = """
        select cs.*
        from charging_station cs
        join charging_station_group g on g.id = cs.group_id
        join location l on l.id = g.location_id
        where cs.is_active = true
          and l.latitude is not null and l.longitude is not null
          and ST_DWithin(
              ST_SetSRID(ST_MakePoint(l.longitude, l.latitude), 4326)::geography,
              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
              (:radiusKm * 1000.0)
          )
          and (:minPower is null or cs.power >= :minPower)
          and (:maxPower is null or cs.power <= :maxPower)
          and (:freeStanding is null or cs.free_standing = :freeStanding)
        """, nativeQuery = true)
    List<ChargingStation> searchActiveWithinRadius(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm,
            @Param("minPower") Double minPower,
            @Param("maxPower") Double maxPower,
            @Param("freeStanding") Boolean freeStanding
    );

    // MAP : markers non paginés dans les bounds
    @Query(value = """
        select
            cs.id as id,
            l.latitude as latitude,
            l.longitude as longitude
        from charging_station cs
        join charging_station_group g on g.id = cs.group_id
        join location l on l.id = g.location_id
        where cs.is_active = true
          and l.latitude is not null and l.longitude is not null
          and ST_Contains(
              ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326),
              ST_SetSRID(ST_MakePoint(l.longitude, l.latitude), 4326)
          )
          and (:minPower is null or cs.power >= :minPower)
          and (:maxPower is null or cs.power <= :maxPower)
          and (:freeStanding is null or cs.free_standing = :freeStanding)
        """, nativeQuery = true)
    List<StationMapMarkerProjection> findMarkersInBounds(
            @Param("swLat") double swLat,
            @Param("swLng") double swLng,
            @Param("neLat") double neLat,
            @Param("neLng") double neLng,
            @Param("minPower") Double minPower,
            @Param("maxPower") Double maxPower,
            @Param("freeStanding") Boolean freeStanding
    );

    // LIST : items paginés dans les mêmes bounds + mêmes filtres
    @Query(
            value = """
            select
                cs.id as id,
                cs.name as name,
                cs.power as power,
                cs.price as price,
                cs.free_standing as freeStanding,
                l.address as address,
                l.zip_code as zipCode,
                l.city as city
            from charging_station cs
            join charging_station_group g on g.id = cs.group_id
            join location l on l.id = g.location_id
            where cs.is_active = true
              and l.latitude is not null and l.longitude is not null
              and ST_Contains(
                  ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326),
                  ST_SetSRID(ST_MakePoint(l.longitude, l.latitude), 4326)
              )
              and (:minPower is null or cs.power >= :minPower)
              and (:maxPower is null or cs.power <= :maxPower)
              and (:freeStanding is null or cs.free_standing = :freeStanding)
            """,
            countQuery = """
            select count(*)
            from charging_station cs
            join charging_station_group g on g.id = cs.group_id
            join location l on l.id = g.location_id
            where cs.is_active = true
              and l.latitude is not null and l.longitude is not null
              and ST_Contains(
                  ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326),
                  ST_SetSRID(ST_MakePoint(l.longitude, l.latitude), 4326)
              )
              and (:minPower is null or cs.power >= :minPower)
              and (:maxPower is null or cs.power <= :maxPower)
              and (:freeStanding is null or cs.free_standing = :freeStanding)
            """,
            nativeQuery = true
    )
    Page<StationListItemProjection> findListInBounds(
            @Param("swLat") double swLat,
            @Param("swLng") double swLng,
            @Param("neLat") double neLat,
            @Param("neLng") double neLng,
            @Param("minPower") Double minPower,
            @Param("maxPower") Double maxPower,
            @Param("freeStanding") Boolean freeStanding,
            Pageable pageable
    );

    Optional<ChargingStation> findByStationGroup(ChargingStationGroup group);
    Optional<ChargingStation> findByNameAndStationGroup(String name, ChargingStationGroup stationGroup);
    boolean existsByNameAndStationGroup(String name, ChargingStationGroup stationGroup);
}
