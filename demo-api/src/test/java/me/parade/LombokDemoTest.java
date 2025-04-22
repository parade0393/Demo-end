package me.parade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LombokDemoTest {
    @Test
    void testLombokGetterSetter(){
        LombokDemo demo = new LombokDemo();
        demo.setId(1L);
        demo.setName("测试");
        assertEquals(1L, demo.getId());
        assertEquals("测试", demo.getName());
    }
}
