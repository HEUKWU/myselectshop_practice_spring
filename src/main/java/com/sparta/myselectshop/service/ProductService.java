package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final FolderRepository folderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        Product product = productRepository.saveAndFlush(new Product(requestDto, user.getId()));

        return new ProductResponseDto(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        UserRoleEnum userRoleEnum = user.getRole();

        Page<Product> products;

        if (userRoleEnum == UserRoleEnum.USER) {
            products = productRepository.findAllByUserId(user.getId(), pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products;
    }

    @Transactional
    public Long updateProduct(Long id, ProductMypriceRequestDto requestDto, User user) {
        Product product = productRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                () -> new NullPointerException("?????? ????????? ???????????? ????????????.")
        );
        product.update(requestDto);

        return product.getId();

    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NullPointerException("?????? ????????? ???????????? ????????????.")
        );
        product.updateByItemDto(itemDto);
    }

    @Transactional
    public Product addFolder(Long productId, Long folderId, User user) {

        // 1) ????????? ???????????????.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NullPointerException("?????? ?????? ???????????? ???????????? ????????????."));

        // 2) ??????????????? ???????????????.
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NullPointerException("?????? ?????? ???????????? ???????????? ????????????."));

        // 3) ????????? ????????? ??????????????? ?????? ???????????? ????????? ???????????? ???????????????.
        Long loginUserId = user.getId();
        if (!product.getUserId().equals(loginUserId) || !folder.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("???????????? ??????????????? ????????????, ???????????? ????????? ????????????~^^");
        }

        // ????????????
        Optional<Product> overlapFolder = productRepository.findByIdAndFolderList_Id(product.getId(), folder.getId());

        if (overlapFolder.isPresent()) {
            throw new IllegalArgumentException("????????? ???????????????.");
        }

        // 4) ????????? ????????? ???????????????.
        product.addFolder(folder);

        return product;
    }

}