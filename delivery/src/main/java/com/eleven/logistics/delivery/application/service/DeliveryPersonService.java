package com.eleven.logistics.delivery.application.service;

import com.eleven.logistics.delivery.domain.entity.DeliveryPerson;
import com.eleven.logistics.delivery.domain.entity.DeliveryPersonType;
import com.eleven.logistics.delivery.domain.repository.DeliveryPersonRepository;
import com.eleven.logistics.delivery.presentation.dtos.CreateDeliveryPersonRequest;
import com.eleven.logistics.delivery.presentation.dtos.DeliveryPersonResponse;
import com.eleven.logistics.delivery.presentation.dtos.UpdateDeliveryPersonRequest;
import com.eleven.logistics.delivery.util.PagingUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryPersonService {

  private final DeliveryPersonRepository deliveryPersonRepository;


  public DeliveryPersonResponse getDeliveryPerson(UUID personId) {
    DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(personId)
        .orElseThrow(() -> new IllegalArgumentException("DeliveryPerson not found"));
    return new DeliveryPersonResponse(deliveryPerson);
  }

  public Page<DeliveryPersonResponse> searchDeliveryPersons(
      DeliveryPersonType type, UUID hubId, Pageable pageable
  ) {
    Pageable persons = PagingUtil.adjustPageable(pageable);
    return deliveryPersonRepository.findByDeliveryPersonTypeAndHubId(type, hubId, persons)
        .map(DeliveryPersonResponse::new);
  }

  @Transactional
  public DeliveryPersonResponse createDeliveryPerson(CreateDeliveryPersonRequest request) {
    DeliveryPerson deliveryPerson = new DeliveryPerson(request);

    deliveryPersonRepository.save(deliveryPerson);
    deliveryPerson.updateCreatedBy(deliveryPerson.getCreatedBy());

    return new DeliveryPersonResponse(deliveryPerson);
  }

  @Transactional
  public DeliveryPersonResponse updateDeliveryPerson(UUID personId,
      UpdateDeliveryPersonRequest request) {
    DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(personId)
        .orElseThrow(() -> new IllegalArgumentException("DeliveryPerson not found"));

    deliveryPerson.update(request);
    deliveryPerson.updateModificationInfo(deliveryPerson.getUpdatedBy());
    deliveryPersonRepository.save(deliveryPerson);

    return new DeliveryPersonResponse(deliveryPerson);
  }

  @Transactional
  public void deleteDeliveryPerson(UUID personId) {
    DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(personId)
        .orElseThrow(() -> new IllegalArgumentException("DeliveryPerson not found"));

    deliveryPerson.updateDeletionInfo(deliveryPerson.getDeletedBy());
  }
}
