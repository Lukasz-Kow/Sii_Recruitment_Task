package sii.task.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sii.task.recruitment.dto.FinancialReportDto;
import sii.task.recruitment.model.FundraisingEvent;

import java.util.List;
import java.util.Optional;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
    Optional<FundraisingEvent> findByEventName(String eventName);

    @Query("SELECT new sii.task.recruitment.dto.FinancialReportDto(f.eventName, f.accountBalance, f.eventCurrency) FROM FundraisingEvent f")
    List<FinancialReportDto> getFinancialReport();
}
