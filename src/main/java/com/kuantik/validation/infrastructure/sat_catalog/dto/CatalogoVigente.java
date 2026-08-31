package com.kuantik.validation.infrastructure.sat_catalog.dto;

/**
 * Contrato mínimo para entradas de catálogo SAT que tienen clave y fecha de fin de vigencia.
 * Implementado por los DTOs de catálogos que requieren validación de vigencia:
 * {@code CatClaveProdServDTO}, {@code CatClaveUnidadDTO}, {@code CatRegimenFiscalDTO},
 * {@code CatUsoCFDIDTO} y {@code CatFormaPagoDTO}.
 */
public interface CatalogoVigente {
    String clave();

    String fechaFinVigencia();
}
