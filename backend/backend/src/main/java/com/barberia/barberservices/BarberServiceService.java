package com.barberia.barberservices;

import com.barberia.barberservices.dto.BarberServiceResponse;
import com.barberia.barberservices.dto.CreateBarberServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarberServiceService {

    private final BarberServiceRepository barberServiceRepository;

    public BarberServiceResponse create(CreateBarberServiceRequest request) {
        BarberService service = new BarberService();
        service.setNombre(request.getNombre());
        service.setPrecio(request.getPrecio());
        service.setDuracionMinutos(request.getDuracionMinutos());
        service.setFinished(false);

        return toResponse(barberServiceRepository.save(service));
    }

    public List<BarberServiceResponse> getAll() {
        return barberServiceRepository.findAll()
                .stream()
                .filter(s -> !s.getFinished())
                .map(this::toResponse)
                .toList();
    }

    public BarberServiceResponse update(Long id, CreateBarberServiceRequest request) {
        BarberService service = barberServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        service.setNombre(request.getNombre());
        service.setPrecio(request.getPrecio());
        service.setDuracionMinutos(request.getDuracionMinutos());

        return toResponse(barberServiceRepository.save(service));
    }

    public void softDelete(Long id) {
        BarberService service = barberServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        service.setFinished(true);
        barberServiceRepository.save(service);
    }

    private BarberServiceResponse toResponse(BarberService service) {
        return new BarberServiceResponse(
                service.getId(),
                service.getNombre(),
                service.getPrecio(),
                service.getDuracionMinutos(),
                service.getFinished()
        );
    }
}