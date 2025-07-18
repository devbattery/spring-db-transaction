package hello.springtx.propagation;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    private PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

    }

    @DisplayName("")
    @Test
    void commit() {
        // given
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        txManager.commit(status);
        log.info("트랜잭션 커밋 완료");
        // when

        // then
    }

    @DisplayName("")
    @Test
    void rollback() {
        // given
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        txManager.rollback(status);
        log.info("트랜잭션 롤백 완료");
        // when

        // then
    }

    @DisplayName("")
    @Test
    void double_commit() {
        // given
        log.info("트랜잭션 1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 1 커밋");
        txManager.commit(tx1);

        log.info("트랜잭션 2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 2 커밋");
        txManager.commit(tx2);

        // when

        // then
    }

    @DisplayName("")
    @Test
    void double_commit_rollback() {
        // given
        log.info("트랜잭션 1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 1 커밋");
        txManager.commit(tx1);

        log.info("트랜잭션 2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 2 롤백");
        txManager.rollback(tx2);

        // when

        // then
    }

    @DisplayName("")
    @Test
    void inner_commit() {
        // given
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());

        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);

        // when

        // then
    }

    @DisplayName("")
    @Test
    void outer_rollback() {
        // given
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        log.info("외부 트랜잭션 롤백");
        txManager.rollback(outer);

        // when

        // then
    }

    @DisplayName("")
    @Test
    void inner_rollback() {
        // given
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner);

        // when
        // then
        log.info("외부 트랜잭션 커밋");
        Assertions.assertThatThrownBy(() -> txManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);
    }

    @DisplayName("")
    @Test
    void inner_rollback_requires_new() {
        // given
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction()); // true

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 옵션 주기
        TransactionStatus inner = txManager.getTransaction(definition);
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction()); // true

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner); // 롤백

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer); // 커밋

        // when

        // then
    }

}
