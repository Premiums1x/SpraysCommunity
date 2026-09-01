package com.lancer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lancer.dto.CheckInVO;
import com.lancer.dto.PageResponse;
import com.lancer.mapper.AnimalMapper;
import com.lancer.mapper.CheckInMapper;
import com.lancer.service.impl.CheckInServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CheckInServiceImplTest {

    @Test
    void loadsMyCheckInsWithOneJoinedQuery() {
        CheckInMapper checkInMapper = mock(CheckInMapper.class);
        AnimalMapper animalMapper = mock(AnimalMapper.class);
        CheckInServiceImpl service = new CheckInServiceImpl(checkInMapper, animalMapper);
        CheckInVO record = new CheckInVO();
        record.setAnimalName("小橘");
        Page<CheckInVO> resultPage = new Page<>(1, 10, 1);
        resultPage.setRecords(List.of(record));
        when(checkInMapper.selectMyCheckIns(org.mockito.ArgumentMatchers.<Page<CheckInVO>>any(), eq(9L))).thenReturn(resultPage);

        PageResponse<CheckInVO> result = service.getMyCheckIns(9L, 1, 10);

        assertThat(result.getRecords()).extracting(CheckInVO::getAnimalName).containsExactly("小橘");
        verify(checkInMapper).selectMyCheckIns(org.mockito.ArgumentMatchers.<Page<CheckInVO>>any(), eq(9L));
        verifyNoInteractions(animalMapper);
    }
}
