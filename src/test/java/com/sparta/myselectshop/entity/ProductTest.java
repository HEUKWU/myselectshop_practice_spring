package com.sparta.myselectshop.entity;

import com.sparta.myselectshop.dto.ProductRequestDto;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Nested
    @DisplayName("회원이 요청한 관심상품 객체 생성")
    class CreateUserProduct {

        private Long userId;
        private String title;
        private String image;
        private String link;
        private int lprice;

        @BeforeEach
        void setup() {
            userId = 100L;
            title = "오리온 꼬북칩 초코츄러스맛 160g";
            image = "https://shopping-phinf.pstatic.net/main_2416122/24161228524.20200915151118.jpg";
            link = "https://search.shopping.naver.com/gate.nhn?id=24161228524";
            lprice = 2350;
        }

        @Test
        @DisplayName("정상 케이스")
        void createProduct_Normal() {
            // given
            ProductRequestDto requestDto = new ProductRequestDto(
                    title,
                    image,
                    link,
                    lprice
            );

            // when
            Product product = new Product(requestDto, userId);

            // then
            assertNull(product.getId());
            assertEquals(userId, product.getUserId());
            assertEquals(title, product.getTitle());
            assertEquals(image, product.getImage());
            assertEquals(link, product.getLink());
            assertEquals(lprice, product.getLprice());
            assertEquals(0, product.getMyprice());
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases {
            @Nested
            @DisplayName("회원 Id")
            class userId {
                @Test
                @DisplayName("null")
                void fail1() {
                    // given
                    userId = null;

                    ProductRequestDto requestDto = new ProductRequestDto(
                            title,
                            image,
                            link,
                            lprice
                    );

                    // when
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        new Product(requestDto, userId);
                    });

                    // then
                    assertEquals("회원 Id 가 유효하지 않습니다.", exception.getMessage());
                }

                @Test
                @DisplayName("마이너스")
                void fail2() {
                    // given
                    userId = -100L;

                    ProductRequestDto requestDto = new ProductRequestDto(
                            title,
                            image,
                            link,
                            lprice
                    );

                    // when
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        new Product(requestDto, userId);
                    });

                    // then
                    assertEquals("회원 Id 가 유효하지 않습니다.", exception.getMessage());
                }
            }

        }
    }
}