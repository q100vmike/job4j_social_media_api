package ru.job4j.socialmedia.controller.presenter;

import org.springframework.stereotype.Component;
import ru.job4j.socialmedia.controller.dto.CreateOfferFriendshipResponse;
import ru.job4j.socialmedia.entity.OfferFriendshipEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class OfferFriendshipPresenter {

    public CreateOfferFriendshipResponse toResponse(OfferFriendshipEntity entity) {
        var response = new CreateOfferFriendshipResponse();
        response.setOfferId(entity.getId());
        response.setFromUserId(entity.getFromUserId());
        response.setToUserId(entity.getToUserId());
        response.setStatus(
                CreateOfferFriendshipResponse.StatusEnum.valueOf(entity.getStatus().name()));
        response.setCreatedAt(OffsetDateTime.ofInstant(entity.getCreatedAt(), ZoneOffset.UTC));
        return response;
    }
}