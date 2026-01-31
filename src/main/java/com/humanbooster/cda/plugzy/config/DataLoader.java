package com.humanbooster.cda.plugzy.config;

import com.humanbooster.cda.plugzy.entity.*;
import com.humanbooster.cda.plugzy.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;

@Profile("dev")
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            LocationRepository locationRepository,
            ChargingStationGroupRepository groupRepository,
            ChargingStationRepository stationRepository,
            BookingRepository bookingRepository, // ✅ AJOUT
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // ---------- ROLES ----------
            Role roleUser = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

            Role roleOwner = roleRepository.findByName("ROLE_OWNER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_OWNER")));


            // ---------- USERS (auth de base) ----------
            userRepository.findByEmail("user@plugzy.test").orElseGet(() -> {
                User u = new User();
                u.setEmail("user@plugzy.test");
                u.setPassword(passwordEncoder.encode("password"));
                u.setRole(roleUser);
                u.setPhone("0600000000");
                u.setUsername("user");
                u.setVerified(true);
                return userRepository.save(u);
            });

            userRepository.findByEmail("admin@plugzy.test").orElseGet(() -> {
                User a = new User();
                a.setEmail("admin@plugzy.test");
                a.setPassword(passwordEncoder.encode("admin"));
                a.setRole(roleAdmin);
                a.setPhone("0600000001");
                a.setUsername("admin");
                a.setVerified(true);
                return userRepository.save(a);
            });


            // ---------- OWNERS ----------
            User owner1 = createOwnerIfMissing(userRepository, passwordEncoder, roleOwner,
                    "owner1@plugzy.test", "owner1", "0600000101");
            User owner2 = createOwnerIfMissing(userRepository, passwordEncoder, roleOwner,
                    "owner2@plugzy.test", "owner2", "0600000102");
            User owner3 = createOwnerIfMissing(userRepository, passwordEncoder, roleOwner,
                    "owner3@plugzy.test", "owner3", "0600000103");
            User owner4 = createOwnerIfMissing(userRepository, passwordEncoder, roleOwner,
                    "owner4@plugzy.test", "owner4", "0600000104");
            User owner5 = createOwnerIfMissing(userRepository, passwordEncoder, roleOwner,
                    "owner5@plugzy.test", "owner5", "0600000105");
            User owner6 = createOwnerIfMissing(userRepository, passwordEncoder, roleOwner,
                    "owner6@plugzy.test", "owner6", "0600000106");

            // Ajout des 2 nouveaux propriétaires demandés
            User owner7 = createOwnerIfMissing(userRepository, passwordEncoder, roleOwner,
                    "owner7@plugzy.test", "owner7", "0600000107");
            User owner8 = createOwnerIfMissing(userRepository, passwordEncoder, roleOwner,
                    "owner8@plugzy.test", "owner8", "0600000108");


            // ---------- LOCATIONS ----------
            // 4 à Lyon
            Location lyon1 = createLocationIfMissing(locationRepository,
                    "10 Place Bellecour", "69002", "Lyon",
                    45.757978, 4.832112, "gmap_lyon_bellecour_001");

            Location lyon2 = createLocationIfMissing(locationRepository,
                    "17 Rue de la Villette", "69003", "Lyon",
                    45.760616, 4.861999, "gmap_lyon_partdieu_002");

            Location lyon3 = createLocationIfMissing(locationRepository,
                    "5 Place de la Croix-Rousse", "69004", "Lyon",
                    45.775728, 4.827604, "gmap_lyon_croixrousse_003");

            Location lyon4 = createLocationIfMissing(locationRepository,
                    "20 Avenue Jean Jaurès", "69007", "Lyon",
                    45.742170, 4.842200, "gmap_lyon_gerland_004");

            // 2 Paris + Marseille
            Location paris1 = createLocationIfMissing(locationRepository,
                    "12 Rue de la Roquette", "75011", "Paris",
                    48.853140, 2.370980, "gmap_paris_11_005");

            Location marseille1 = createLocationIfMissing(locationRepository,
                    "2 Place Castellane", "13006", "Marseille",
                    43.285280, 5.382110, "gmap_marseille_castellane_006");


            // 8 nouvelles locations : Villeurbanne + Sud-Est Lyon
            Location villeurbanne1 = createLocationIfMissing(locationRepository,
                    "25 Cours Émile Zola", "69100", "Villeurbanne",
                    45.7709, 4.8806, "gmap_villeurbanne_zola_101");

            Location villeurbanne2 = createLocationIfMissing(locationRepository,
                    "2 Place Grandclément", "69100", "Villeurbanne",
                    45.7619, 4.8958, "gmap_villeurbanne_grandclement_102");

            Location bron = createLocationIfMissing(locationRepository,
                    "1 Avenue Franklin Roosevelt", "69500", "Bron",
                    45.7386, 4.9134, "gmap_bron_roosevelt_103");

            Location venissieux = createLocationIfMissing(locationRepository,
                    "10 Boulevard Ambroise Croizat", "69200", "Vénissieux",
                    45.6996, 4.8850, "gmap_venissieux_croizat_104");

            Location saintPriest = createLocationIfMissing(locationRepository,
                    "Place Charles Ottina", "69800", "Saint-Priest",
                    45.6960, 4.9447, "gmap_saintpriest_ottina_105");

            Location corbas = createLocationIfMissing(locationRepository,
                    "1 Rue du 8 Mai 1945", "69960", "Corbas",
                    45.6640, 4.9017, "gmap_corbas_8mai_106");

            Location feyzin = createLocationIfMissing(locationRepository,
                    "5 Place de l'Europe", "69320", "Feyzin",
                    45.6726, 4.8589, "gmap_feyzin_europe_107");

            Location mions = createLocationIfMissing(locationRepository,
                    "Place de la République", "69780", "Mions",
                    45.6649, 4.9526, "gmap_mions_republique_108");


            // AJOUT : +2 Villeurbanne
            Location villeurbanne3 = createLocationIfMissing(locationRepository,
                    "90 Rue Roger Salengro", "69100", "Villeurbanne",
                    45.7682, 4.9009, "gmap_villeurbanne_salengro_109");

            Location villeurbanne4 = createLocationIfMissing(locationRepository,
                    "15 Rue Paul Verlaine", "69100", "Villeurbanne",
                    45.7840, 4.8942, "gmap_villeurbanne_verlaine_110");


            // AJOUT DEMANDÉ : +4 Lyon (3e, 7e, 8e)
            // -> 2 en 3e, 1 en 7e, 1 en 8e
            Location lyon3b = createLocationIfMissing(locationRepository,
                    "65 Cours Lafayette", "69003", "Lyon",
                    45.7639, 4.8536, "gmap_lyon3_lafayette_201");

            Location lyon3c = createLocationIfMissing(locationRepository,
                    "108 Rue Garibaldi", "69003", "Lyon",
                    45.7572, 4.8508, "gmap_lyon3_garibaldi_202");

            Location lyon7b = createLocationIfMissing(locationRepository,
                    "50 Rue de Marseille", "69007", "Lyon",
                    45.7504, 4.8429, "gmap_lyon7_marseille_203");

            Location lyon8 = createLocationIfMissing(locationRepository,
                    "3 Avenue des Frères Lumière", "69008", "Lyon",
                    45.7450, 4.8688, "gmap_lyon8_lumiere_204");


            // ---------- GROUPS + STATIONS (existants) ----------
            createGroupAndStationIfMissing(groupRepository, stationRepository,
                    owner1, lyon1,
                    "Borne Bellecour (Lyon 2e)",
                    "Borne domestique idéale pour une recharge lente en centre-ville.",
                    "Wallbox Bellecour", 3.7, 2.50, true, false);

            createGroupAndStationIfMissing(groupRepository, stationRepository,
                    owner2, lyon2,
                    "Borne Part-Dieu (Lyon 3e)",
                    "Recharge accélérée proche gare / Part-Dieu.",
                    "Wallbox Part-Dieu", 7.4, 3.20, true, true);

            createGroupAndStationIfMissing(groupRepository, stationRepository,
                    owner3, lyon3,
                    "Borne Croix-Rousse (Lyon 4e)",
                    "Recharge triphasée 11 kW (selon véhicule) dans quartier calme.",
                    "Borne Croix-Rousse", 11.0, 3.90, true, false);

            createGroupAndStationIfMissing(groupRepository, stationRepository,
                    owner4, lyon4,
                    "Borne Gerland (Lyon 7e)",
                    "Recharge rapide AC 22 kW, pratique pour courts arrêts.",
                    "Borne Gerland 22", 22.0, 5.50, true, true);

            createGroupAndStationIfMissing(groupRepository, stationRepository,
                    owner5, paris1,
                    "Borne Paris 11e",
                    "Station en parking privé, accessible sur réservation.",
                    "Wallbox Roquette", 7.4, 4.50, true, false);

            createGroupAndStationIfMissing(groupRepository, stationRepository,
                    owner6, marseille1,
                    "Borne Castellane (Marseille)",
                    "Recharge AC 11 kW dans le 6e, accès facile.",
                    "Borne Castellane", 11.0, 4.00, true, false);


            // ---------- NOUVELLES BORNES (2 owners, 8 stations) ----------
            // Owner7 : 4 bornes
            ChargingStationGroup gVbz1 = createGroupIfMissing(groupRepository, owner7, villeurbanne1,
                    "Villeurbanne - Gratte-Ciel",
                    "Parking résidentiel, accès simple, recharge AC.");

            createStationIfMissing(stationRepository, gVbz1,
                    "Gratte-Ciel A", 7.4, 3.20, true, true);

            // Ici, on met 2 bornes dans le MÊME GROUPE (exigence)
            createStationIfMissing(stationRepository, gVbz1,
                    "Gratte-Ciel B", 7.4, 3.20, true, true);

            ChargingStationGroup gVbz2 = createGroupIfMissing(groupRepository, owner7, villeurbanne2,
                    "Villeurbanne - Grandclément",
                    "Borne proche commerces, idéale recharge pendant courses.");

            createStationIfMissing(stationRepository, gVbz2,
                    "Grandclément", 11.0, 3.80, true, false);

            ChargingStationGroup gBron = createGroupIfMissing(groupRepository, owner7, bron,
                    "Bron - Roosevelt",
                    "Recharge sécurisée en copropriété, accès par badge.");

            createStationIfMissing(stationRepository, gBron,
                    "Roosevelt", 7.4, 3.10, true, true);


            // Owner8 : 4 bornes
            ChargingStationGroup gVenissieux = createGroupIfMissing(groupRepository, owner8, venissieux,
                    "Vénissieux - Croizat",
                    "Recharge AC 11 kW_toggle, zone calme et facile d'accès.");

            createStationIfMissing(stationRepository, gVenissieux,
                    "Chez Raymond", 11.0, 3.60, true, false);

            ChargingStationGroup gStPriest = createGroupIfMissing(groupRepository, owner8, saintPriest,
                    "Saint-Priest - Centre",
                    "Recharge accélérée 22 kW (AC), idéale trajets pendulaires.");

            createStationIfMissing(stationRepository, gStPriest,
                    "Chez Martine", 22.0, 5.20, true, true);

            ChargingStationGroup gCorbas = createGroupIfMissing(groupRepository, owner8, corbas,
                    "Corbas - 8 Mai 1945",
                    "Borne domestique, prix doux, créneaux larges.");

            createStationIfMissing(stationRepository, gCorbas,
                    "Corbas centre", 3.7, 2.30, true, false);

            ChargingStationGroup gFeyzin = createGroupIfMissing(groupRepository, owner8, feyzin,
                    "Feyzin - Place de l'Europe",
                    "Recharge pratique proche A7, accès simple.");

            createStationIfMissing(stationRepository, gFeyzin,
                    "Feyzin Europe", 7.4, 3.00, true, true);

            ChargingStationGroup gMions = createGroupIfMissing(groupRepository, owner8, mions,
                    "Mions - République",
                    "Recharge résidentielle, très pratique en soirée / nuit.");

            createStationIfMissing(stationRepository, gMions,
                    "Wallbox Mions", 7.4, 2.90, true, false);


            // ---------- AJOUT : 2 bornes à Villeurbanne ----------
            ChargingStationGroup gVbz3 = createGroupIfMissing(groupRepository, owner7, villeurbanne3,
                    "Villeurbanne - Salengro",
                    "Borne en parking privé, accès simple et rapide.");

            createStationIfMissing(stationRepository, gVbz3,
                    "Salengro", 7.4, 3.10, true, true);

            ChargingStationGroup gVbz4 = createGroupIfMissing(groupRepository, owner8, villeurbanne4,
                    "Villeurbanne - Verlaine",
                    "Recharge AC idéale pour stationnement longue durée.");

            createStationIfMissing(stationRepository, gVbz4,
                    "Verlaine", 11.0, 3.70, true, false);


            // ---------- AJOUT : 4 bornes Lyon (3e, 7e, 8e) ----------
            ChargingStationGroup gLyon3b = createGroupIfMissing(groupRepository, owner7, lyon3b,
                    "Lyon 3e - Cours Lafayette",
                    "Recharge pratique proche Part-Dieu, accès sécurisé.");

            createStationIfMissing(stationRepository, gLyon3b,
                    "Lafayette", 7.4, 3.40, true, true);

            ChargingStationGroup gLyon3c = createGroupIfMissing(groupRepository, owner8, lyon3c,
                    "Lyon 3e - Garibaldi",
                    "Borne en copropriété, idéale trajets pendulaires.");

            createStationIfMissing(stationRepository, gLyon3c,
                    "Garibaldi", 11.0, 3.90, true, false);

            ChargingStationGroup gLyon7c = createGroupIfMissing(groupRepository, owner7, lyon7b,
                    "Lyon 7e - Rue de Marseille",
                    "Recharge accessible près des quais, pratique en soirée.");

            createStationIfMissing(stationRepository, gLyon7c,
                    "Marseille 7e", 7.4, 3.00, true, false);

            ChargingStationGroup gLyon8b = createGroupIfMissing(groupRepository, owner8, lyon8,
                    "Lyon 8e - Lumière",
                    "Recharge AC 22 kW idéale pour arrêts courts.");

            createStationIfMissing(stationRepository, gLyon8b,
                    "Chez Michel", 22.0, 5.10, true, true);


            // ---------- BOOKING (AJOUT DEMANDÉ) ----------
            // Booking sur "Gratte-Ciel A" : 18/02/2026 14:00 -> 18/02/2026 18:00
            User bookingUser = userRepository.findByEmail("user@plugzy.test")
                    .orElseThrow(() -> new IllegalStateException("User not found: user@plugzy.test"));

            ChargingStation gratteCielA = stationRepository.findByNameAndStationGroup("Gratte-Ciel A", gVbz1)
                    .orElseThrow(() -> new IllegalStateException("Station not found: Gratte-Ciel A"));

            LocalDateTime start = LocalDateTime.of(2026, 2, 18, 14, 0);
            LocalDateTime end = LocalDateTime.of(2026, 2, 18, 18, 0);

            boolean bookingExists = bookingRepository.existsByChargingStationAndStartTimeAndEndTime(gratteCielA, start, end);
            if (!bookingExists) {
                double hours = Duration.between(start, end).toMinutes() / 60.0;
                double totalPrice = gratteCielA.getPrice() * hours;

                Booking b = new Booking();
                b.setUser(bookingUser);
                b.setChargingStation(gratteCielA);
                b.setStartTime(start);
                b.setEndTime(end);
                b.setTotalPrice(totalPrice);
                b.setStatus(1); // adapte selon ta convention

                bookingRepository.save(b);
            }
        };
    }

    private User createOwnerIfMissing(UserRepository userRepository,
                                      PasswordEncoder encoder,
                                      Role roleOwner,
                                      String email,
                                      String username,
                                      String phone) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User o = new User();
            o.setEmail(email);
            o.setPassword(encoder.encode("password"));
            o.setRole(roleOwner);
            o.setPhone(phone);
            o.setUsername(username);
            o.setVerified(true);
            return userRepository.save(o);
        });
    }

    private Location createLocationIfMissing(LocationRepository locationRepository,
                                             String address,
                                             String zipCode,
                                             String city,
                                             Double lat,
                                             Double lng,
                                             String gmapId) {
        return locationRepository.findByGmapId(gmapId).orElseGet(() -> {
            Location loc = new Location();
            loc.setAddress(address);
            loc.setZipCode(zipCode);
            loc.setCity(city);
            loc.setLatitude(lat);
            loc.setLongitude(lng);
            loc.setGmapId(gmapId);
            return locationRepository.save(loc);
        });
    }

    // Nouveau : permet plusieurs groupes par owner (1 groupe = 1 lieu + 1 owner)
    private ChargingStationGroup createGroupIfMissing(ChargingStationGroupRepository groupRepository,
                                                      User owner,
                                                      Location location,
                                                      String title,
                                                      String description) {

        return groupRepository.findByOwnerAndTitle(owner, title).orElseGet(() -> {
            ChargingStationGroup g = new ChargingStationGroup();
            g.setOwner(owner);
            g.setLocation(location);
            g.setTitle(title);
            g.setDescription(description);
            return groupRepository.save(g);
        });
    }

    // ✅ Nouveau : permet plusieurs stations dans un même groupe
    private void createStationIfMissing(ChargingStationRepository stationRepository,
                                        ChargingStationGroup group,
                                        String stationName,
                                        Double power,
                                        Double pricePerHour,
                                        boolean isActive,
                                        boolean freeStanding) {

        boolean exists = stationRepository.existsByNameAndStationGroup(stationName, group);
        if (exists) return;

        ChargingStation s = new ChargingStation();
        s.setName(stationName);
        s.setPower(power);
        s.setPrice(pricePerHour);
        s.setActive(isActive);
        s.setFreeStanding(freeStanding);
        s.setGroup(group);
        stationRepository.save(s);
    }

    // (Ton helper existant inchangé)
    private void createGroupAndStationIfMissing(ChargingStationGroupRepository groupRepository,
                                                ChargingStationRepository stationRepository,
                                                User owner,
                                                Location location,
                                                String groupTitle,
                                                String groupDesc,
                                                String stationName,
                                                Double power,
                                                Double pricePerHour,
                                                boolean isActive,
                                                boolean freeStanding) {

        ChargingStationGroup group = groupRepository.findByOwnerAndTitle(owner, groupTitle).orElseGet(() -> {
            ChargingStationGroup g = new ChargingStationGroup();
            g.setOwner(owner);
            g.setLocation(location);
            g.setTitle(groupTitle);
            g.setDescription(groupDesc);
            return groupRepository.save(g);
        });

        boolean exists = stationRepository.existsByNameAndStationGroup(stationName, group);
        if (exists) return;

        ChargingStation s = new ChargingStation();
        s.setName(stationName);
        s.setPower(power);
        s.setPrice(pricePerHour);
        s.setActive(isActive);
        s.setFreeStanding(freeStanding);
        s.setGroup(group);
        stationRepository.save(s);
    }
}
