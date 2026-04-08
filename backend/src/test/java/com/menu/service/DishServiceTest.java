package com.menu.service;

import com.menu.common.PageResult;
import com.menu.dto.DishDTO;
import com.menu.entity.Dish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("菜品服务测试")
class DishServiceTest {

    @Autowired
    private DishService dishService;

    @Test
    @DisplayName("分页查询菜品")
    void testPage() {
        PageResult<Dish> result = dishService.page(1, 10, null, null);

        assertNotNull(result);
        assertEquals(3, result.getTotal());
        assertFalse(result.getRecords().isEmpty());
    }

    @Test
    @DisplayName("按分类查询菜品")
    void testPageByCategory() {
        PageResult<Dish> result = dishService.page(1, 10, 1L, null);

        assertNotNull(result);
        assertTrue(result.getRecords().stream()
                .allMatch(d -> d.getCategoryId().equals(1L)));
    }

    @Test
    @DisplayName("关键字搜索菜品")
    void testPageByKeyword() {
        PageResult<Dish> result = dishService.page(1, 10, null, "煎蛋");

        assertNotNull(result);
        assertFalse(result.getRecords().isEmpty());
        assertTrue(result.getRecords().get(0).getName().contains("煎蛋"));
    }

    @Test
    @DisplayName("按分类获取菜品列表")
    void testListByCategory() {
        List<Dish> dishes = dishService.listByCategory(1L);

        assertNotNull(dishes);
        assertTrue(dishes.stream().allMatch(d -> d.getCategoryId().equals(1L)));
    }

    @Test
    @DisplayName("随机推荐菜品")
    void testRandom() {
        List<Dish> dishes = dishService.random(2);

        assertNotNull(dishes);
        assertTrue(dishes.size() <= 2);
    }

    @Test
    @DisplayName("获取菜品详情")
    void testGetById() {
        Dish dish = dishService.getById(1L);

        assertNotNull(dish);
        assertEquals("爱心煎蛋", dish.getName());
    }

    @Test
    @DisplayName("新增菜品")
    void testSaveNew() {
        DishDTO dto = new DishDTO();
        dto.setCategoryId(1L);
        dto.setName("测试菜品");
        dto.setDescription("测试描述");
        dto.setDifficulty(2);
        dto.setCookTime(20);

        dishService.save(dto);

        // 验证新增成功
        PageResult<Dish> result = dishService.page(1, 10, null, "测试菜品");
        assertFalse(result.getRecords().isEmpty());
    }

    @Test
    @DisplayName("更新菜品")
    void testSaveUpdate() {
        DishDTO dto = new DishDTO();
        dto.setId(1L);
        dto.setCategoryId(1L);
        dto.setName("爱心煎蛋更新");
        dto.setDifficulty(2);
        dto.setCookTime(15);

        dishService.save(dto);

        Dish dish = dishService.getById(1L);
        assertEquals("爱心煎蛋更新", dish.getName());
    }

    @Test
    @DisplayName("增加点餐次数")
    void testIncrementOrderCount() {
        Dish before = dishService.getById(1L);
        int beforeCount = before.getOrderCount();

        dishService.incrementOrderCount(1L);

        Dish after = dishService.getById(1L);
        assertEquals(beforeCount + 1, after.getOrderCount());
    }
}
