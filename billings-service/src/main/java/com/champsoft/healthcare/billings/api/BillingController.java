package com.champsoft.healthcare.billings.api;

import com.champsoft.healthcare.billings.api.dto.CreateBillingRequest;
import com.champsoft.healthcare.billings.api.dto.UpdateBillingRequest;
import com.champsoft.healthcare.billings.api.mapper.BillingApiMapper;
import com.champsoft.healthcare.billings.application.service.BillingCrudService;
import com.champsoft.healthcare.billings.domain.model.DueDate;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingCrudService service;

    public BillingController(BillingCrudService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateBillingRequest req){
        var v = service.create(req.description(),req.amount(),new DueDate(req.dueDate()),req.method());
        return ResponseEntity.ok(BillingApiMapper.toResponse(v));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id){
        return ResponseEntity.ok(BillingApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping
    public ResponseEntity<?> list(){
        return ResponseEntity.ok(
                service.list()
                        .stream()
                        .map(BillingApiMapper::toResponse)
                        .toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid UpdateBillingRequest req){
        var v = service.updateBillingItem(id,req.description(),req.amount());
        return ResponseEntity.ok(BillingApiMapper.toResponse(v));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/paid")
    public ResponseEntity<?> paid(@PathVariable String id){
        var billing = service.paid(id);
        return ResponseEntity.ok(BillingApiMapper.toResponse(billing));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<?> refunded(@PathVariable String id){
        var billing = service.refunded(id);
        return ResponseEntity.ok(BillingApiMapper.toResponse(billing));
    }


}
