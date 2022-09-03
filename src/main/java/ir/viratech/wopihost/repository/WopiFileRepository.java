package ir.viratech.wopihost.repository;

import ir.viratech.wopihost.entity.WopiFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WopiFileRepository extends JpaRepository<WopiFile, Long> {

    @Query("SELECT w from WopiFile w where w.isSessionActive = false AND w.clientDefinedIdentifier = :identifier")
    WopiFile findFirstNonActiveFile(@Param("identifier") String identifier);

    WopiFile getFirstByClientDefinedIdentifierAndClientName(String identifier, String clientName);
}
