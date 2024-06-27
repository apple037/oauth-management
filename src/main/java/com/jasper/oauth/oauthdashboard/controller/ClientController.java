package com.jasper.oauth.oauthdashboard.controller;

import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.Result;
import com.jasper.oauth.oauthdashboard.model.client.ClientCreateAndUpdateRequest;
import com.jasper.oauth.oauthdashboard.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Client", description = "Client management")
@RestController
@RequestMapping("oauth/api/client/")
public class ClientController {
  private final ClientService clientService;

  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @Operation(summary = "Get client list", description = "Get client list in pages")
  @GetMapping("v1/list")
  public Object list(PageParam pageParam) {
    try {
      return Result.success(clientService.getClientList(pageParam));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Get client detail", description = "Get client detail by client id")
  @GetMapping("v1/{clientId}/detail")
  public Object detail(@PathVariable(name = "clientId") Integer clientId) {
    try {
      return Result.success(clientService.getClientDetail(clientId));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Create client", description = "Create client")
  @PostMapping("v1/create")
  public Object create(@RequestBody @Valid ClientCreateAndUpdateRequest request) {
    try {
      return Result.success(clientService.createClient(request));
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Update client", description = "Update client by client id")
  @PutMapping("v1/{clientId}/update")
  public Object update(
      @PathVariable(name = "clientId") Integer clientId,
      @RequestBody @Valid ClientCreateAndUpdateRequest request) {
    try {
      // TODO validation should be different for create and update
      clientService.updateClient(clientId, request);
      return Result.success();
    } catch (Exception e) {
      return Result.fail(e);
    }
  }

  @Operation(summary = "Delete client", description = "Delete client by client id")
  @DeleteMapping("v1/{clientId}/delete")
  public Object delete(@PathVariable(name = "clientId") Integer clientId) {
    try {
      clientService.deleteClient(clientId);
      return Result.success();
    } catch (Exception e) {
      return Result.fail(e);
    }
  }
}
