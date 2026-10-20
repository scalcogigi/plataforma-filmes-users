package org.example.strategy;

import org.example.entity.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NetworkRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<User> recommend(User current, List<User> users, int limit) {
        Map<String, User> byId = users.stream().filter(u -> u.getId() != null)
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
        Set<String> excluded = new HashSet<>(nonNull(current.getSeguindoIds()));
        excluded.add(current.getId());
        Set<String> direct = new HashSet<>(nonNull(current.getSeguindoIds()));
        direct.addAll(nonNull(current.getSeguidoresIds()));
        Set<String> networkIds = new HashSet<>();
        for (String id : direct) {
            User relation = byId.get(id);
            if (relation != null) {
                networkIds.addAll(nonNull(relation.getSeguindoIds()));
                networkIds.addAll(nonNull(relation.getSeguidoresIds()));
            }
        }
        Comparator<User> relevance = Comparator.comparingInt(this::total).reversed()
                .thenComparing(Comparator.comparingInt(this::followers).reversed())
                .thenComparing(User::getId);
        List<User> result = networkIds.stream().map(byId::get).filter(Objects::nonNull)
                .filter(u -> !excluded.contains(u.getId())).distinct().sorted(relevance)
                .limit(limit).collect(Collectors.toCollection(ArrayList::new));
        if (result.size() < limit) {
            Set<String> selected = result.stream().map(User::getId).collect(Collectors.toSet());
            users.stream().filter(u -> u.getId() != null && !excluded.contains(u.getId()) && !selected.contains(u.getId()))
                    .sorted(Comparator.comparingInt(this::following).reversed()
                            .thenComparing(Comparator.comparingInt(this::followers).reversed())
                            .thenComparing(User::getId))
                    .limit(limit - result.size()).forEach(result::add);
        }
        return result;
    }

    private static List<String> nonNull(List<String> ids) { return ids == null ? List.of() : ids; }
    private int followers(User u) { return new HashSet<>(nonNull(u.getSeguidoresIds())).size(); }
    private int following(User u) { return new HashSet<>(nonNull(u.getSeguindoIds())).size(); }
    private int total(User u) { return followers(u) + following(u); }
}
