package com.lancer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lancer.common.exception.BusinessException;
import com.lancer.dto.CheckInRequest;
import com.lancer.dto.CheckInVO;
import com.lancer.entity.Animal;
import com.lancer.entity.CheckIn;
import com.lancer.mapper.AnimalMapper;
import com.lancer.mapper.CheckInMapper;
import com.lancer.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceImplTest {

    @Mock
    private CheckInMapper checkInMapper;

    @Mock
    private AnimalMapper animalMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CheckInServiceImpl checkInService;

    @Test
    void createCheckIn_shouldSucceed() {
        CheckInRequest request = new CheckInRequest(1L, "今天看到小花了", false);
        when(animalMapper.selectById(1L)).thenReturn(new Animal());
        when(checkInMapper.insert(any(CheckIn.class))).thenReturn(1);

        checkInService.createCheckIn(request, 1L);

        verify(checkInMapper).insert(any(CheckIn.class));
    }

    @Test
    void createCheckIn_shouldSucceedWithAnonymousTrue() {
        CheckInRequest request = new CheckInRequest(1L, "匿名打卡", true);
        when(animalMapper.selectById(1L)).thenReturn(new Animal());
        when(checkInMapper.insert(any(CheckIn.class))).thenReturn(1);

        checkInService.createCheckIn(request, 1L);

        verify(checkInMapper).insert(any(CheckIn.class));
    }

    @Test
    void createCheckIn_shouldThrowWhenAnimalNotFound() {
        CheckInRequest request = new CheckInRequest(999L, "不存在", false);
        when(animalMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> checkInService.createCheckIn(request, 1L));
        assertEquals(404, ex.getCode());
        assertEquals("动物档案不存在", ex.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getCheckInsByAnimalId_shouldReturnTimeline() {
        Map<String, Object> record = new HashMap<>();
        record.put("id", 1L);
        record.put("user_id", 1L);
        record.put("animal_id", 1L);
        record.put("content", "看到了小花");
        record.put("is_anonymous", false);
        record.put("create_time", LocalDateTime.now());
        record.put("username", "admin");
        record.put("user_nickname", "管理员");
        record.put("user_avatar", null);

        Page<Map<String, Object>> mapPage = new Page<>(1, 10);
        mapPage.setRecords(List.of(record));
        mapPage.setTotal(1);

        when(checkInMapper.selectCheckInWithUser(any(Page.class), eq(1L))).thenReturn(mapPage);

        IPage<CheckInVO> result = checkInService.getCheckInsByAnimalId(1L, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        CheckInVO vo = result.getRecords().get(0);
        assertEquals("admin", vo.getUsername());
        assertEquals("管理员", vo.getUserDisplayName());
    }

    @Test
    void getCheckInsByAnimalId_shouldHideUserInfoWhenAnonymous() {
        Map<String, Object> record = new HashMap<>();
        record.put("id", 1L);
        record.put("user_id", 1L);
        record.put("animal_id", 1L);
        record.put("content", "匿名内容");
        record.put("is_anonymous", true);
        record.put("create_time", LocalDateTime.now());
        record.put("username", "user");
        record.put("user_nickname", "小明");
        record.put("user_avatar", null);

        Page<Map<String, Object>> mapPage = new Page<>(1, 10);
        mapPage.setRecords(List.of(record));
        mapPage.setTotal(1);

        when(checkInMapper.selectCheckInWithUser(any(Page.class), eq(1L))).thenReturn(mapPage);

        IPage<CheckInVO> result = checkInService.getCheckInsByAnimalId(1L, 1, 10);

        CheckInVO vo = result.getRecords().get(0);
        assertNull(vo.getUsername());
        assertEquals("匿名用户", vo.getUserDisplayName());
    }
}
