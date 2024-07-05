package hello.springtx.apply;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
@Slf4j
public class TxBasicTest {

    @Autowired
    private BasicService basicService;

    @DisplayName("")
    @Test
    void proxyCheck() {
        log.info("aop class={}", basicService.getClass());

        // given
        // when
        // then
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    @DisplayName("")
    @Test
    void txTest() {
        basicService.tx();
        basicService.nonTx();
        // given
        // when
        // then
    }

    @TestConfiguration
    static class TxApplyBasicConfig {

        @Bean
        BasicService basicService() {
            return new BasicService();
        }

    }

    @Slf4j
    static class BasicService {

        @Transactional
        public void tx() {
            log.info("call tx");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }

        public void nonTx() {
            log.info("call nonTx");
            boolean nonTxActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("nonTx active={}", nonTxActive);
        }

    }

}
