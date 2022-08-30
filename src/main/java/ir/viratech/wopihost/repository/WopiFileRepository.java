package ir.viratech.wopihost.repository;

import ir.viratech.wopihost.entity.WopiFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WopiFileRepository extends JpaRepository<WopiFile, Long> {

    WopiFile getFirstByClientDefinedIdentifierAndClientName(String identifier, String clientName);
}
