package com.github.dynodao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.github.dynodao.test.AbstractUnitTest;
import com.github.dynodao.test.Item;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DynoDaoTest extends AbstractUnitTest {

    private static final Item ITEM = Item.item("item", 0);
    private static final int SEGMENTS = 4;

    @Mock private DynoDaoLoad<Item> loadMock;
    @Mock private DynoDaoQuery<Item> queryMock;
    @Mock private DynoDaoScan<Item> scanMock;

    @Mock private AmazonDynamoDB amazonDynamoDbMock;

    @InjectMocks private DynoDao classUnderTest;

    @Test
    void get_load_returnsLoadResult() {
        Stream<Item> load = Stream.of(ITEM);
        when(loadMock.load(amazonDynamoDbMock)).thenReturn(load);
        assertThat(classUnderTest.get(loadMock)).isSameAs(load);
        verify(loadMock).load(amazonDynamoDbMock);
        verifyNoMoreInteractions(loadMock, queryMock, scanMock, amazonDynamoDbMock);
    }

    @Test
    void get_query_returnsQueryResult() {
        Stream<Item> query = Stream.of(ITEM);
        when(queryMock.query(amazonDynamoDbMock)).thenReturn(query);
        assertThat(classUnderTest.get(queryMock)).isSameAs(query);
        verify(queryMock).query(amazonDynamoDbMock);
        verifyNoMoreInteractions(loadMock, queryMock, scanMock, amazonDynamoDbMock);
    }

    @Test
    void get_scan_returnsScanResult() {
        Stream<Item> scan = Stream.of(ITEM);
        when(scanMock.scan(amazonDynamoDbMock)).thenReturn(scan);
        assertThat(classUnderTest.get(scanMock)).isSameAs(scan);
        verify(scanMock).scan(amazonDynamoDbMock);
        verifyNoMoreInteractions(loadMock, queryMock, scanMock, amazonDynamoDbMock);
    }

    @Test
    void get_parallelScan_returnsParallelScanResult() {
        Stream<Item> scan = Stream.of(ITEM);
        when(scanMock.parallelScan(amazonDynamoDbMock, SEGMENTS)).thenReturn(scan);
        assertThat(classUnderTest.get(scanMock, SEGMENTS)).isSameAs(scan);
        verify(scanMock).parallelScan(amazonDynamoDbMock, SEGMENTS);
        verifyNoMoreInteractions(loadMock, queryMock, scanMock, amazonDynamoDbMock);
    }

}
