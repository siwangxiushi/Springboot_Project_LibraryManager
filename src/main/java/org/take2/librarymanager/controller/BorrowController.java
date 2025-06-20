package org.take2.librarymanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.take2.librarymanager.model.Borrow;
import org.take2.librarymanager.service.IBorrowService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.take2.librarymanager.service.impl.BorrowServiceImpl;

@RequiredArgsConstructor
@RestController
@RequestMapping("/borrow")
public class BorrowController {

    private final IBorrowService borrowService;

    /**
     * 响应体：封装借阅记录和对应馆藏状态信息
     */
    public record BorrowResponse(
            Long id,
            Instant borrowDate,
            Long userId,
            Long collectionId,
            Instant returnDeadline,
            Instant returnDate,
            Integer renewedTimes,
            BigDecimal finePaid,
            Boolean collectionIsBorrowable
    ) {}

    /**
     * 请求体：用于新增借阅记录
     */
    public record BorrowAddRequest(
            Long userId,
            Long collectionId,
            Instant returnDeadline
    ) {}

    /**
     * 请求体：用于更新借阅记录，允许修改续借次数、罚款、归还日期及对应馆藏的可借状态
     */
    public record BorrowUpdateRequest(
            Integer renewedTimes,
            BigDecimal finePaid,
            Instant returnDate,
            Boolean collectionIsBorrowable
    ) {}

    /**
     * 管理员获取所有借阅记录
     * GET /borrow/list?page=1
     */
    @GetMapping("/list")
    public List<BorrowResponse> getAllBorrows(@RequestParam int page) {
        Page<BorrowServiceImpl.BorrowVO> result = borrowService.getAllBorrows(page);
        return result.getRecords().stream()
                .map(vo -> new BorrowResponse(
                        vo.id(),
                        vo.borrowDate(),
                        vo.userId(),
                        vo.collectionId(),
                        vo.returnDeadline(),
                        vo.returnDate(),
                        vo.renewedTimes(),
                        vo.finePaid(),
                        vo.collectionIsBorrowable()))
                .collect(Collectors.toList());
    }

    /**
     * 管理员获取未完成的借阅记录
     * GET /borrow/incomplete?page=1
     */
    @GetMapping("/incomplete")
    public List<BorrowResponse> getIncompleteBorrows(@RequestParam int page) {
        Page<BorrowServiceImpl.BorrowVO> result = borrowService.getIncompleteBorrows(page);
        return result.getRecords().stream()
                .map(vo -> new BorrowResponse(
                        vo.id(),
                        vo.borrowDate(),
                        vo.userId(),
                        vo.collectionId(),
                        vo.returnDeadline(),
                        vo.returnDate(),
                        vo.renewedTimes(),
                        vo.finePaid(),
                        vo.collectionIsBorrowable()))
                .collect(Collectors.toList());
    }

    /**
     * 管理员根据用户ID查询借阅记录
     * GET /borrow/user?userId=xxx&page=1
     */
    @GetMapping("/user")
    public List<BorrowResponse> getBorrowsByUser(@RequestParam Long userId, @RequestParam int page) {
        Page<BorrowServiceImpl.BorrowVO> result = borrowService.getBorrowsByUser(userId, page);
        return result.getRecords().stream()
                .map(vo -> new BorrowResponse(
                        vo.id(),
                        vo.borrowDate(),
                        vo.userId(),
                        vo.collectionId(),
                        vo.returnDeadline(),
                        vo.returnDate(),
                        vo.renewedTimes(),
                        vo.finePaid(),
                        vo.collectionIsBorrowable()))
                .collect(Collectors.toList());
    }

    /**
     * 用户获取自己的所有借阅记录
     * GET /borrow/my?page=1
     */
    @GetMapping("/my")
    public List<BorrowResponse> getMyBorrows(@RequestParam int page) {
        Page<BorrowServiceImpl.BorrowVO> result = borrowService.getMyBorrows(page);
        return result.getRecords().stream()
                .map(vo -> new BorrowResponse(
                        vo.id(),
                        vo.borrowDate(),
                        vo.userId(),
                        vo.collectionId(),
                        vo.returnDeadline(),
                        vo.returnDate(),
                        vo.renewedTimes(),
                        vo.finePaid(),
                        vo.collectionIsBorrowable()))
                .collect(Collectors.toList());
    }

    /**
     * 用户获取自己未完成的借阅记录
     * GET /borrow/my/incomplete?page=1
     */
    @GetMapping("/my/incomplete")
    public List<BorrowResponse> getMyIncompleteBorrows(@RequestParam int page) {
        Page<BorrowServiceImpl.BorrowVO> result = borrowService.getMyIncompleteBorrows(page);
        return result.getRecords().stream()
                .map(vo -> new BorrowResponse(
                        vo.id(),
                        vo.borrowDate(),
                        vo.userId(),
                        vo.collectionId(),
                        vo.returnDeadline(),
                        vo.returnDate(),
                        vo.renewedTimes(),
                        vo.finePaid(),
                        vo.collectionIsBorrowable()))
                .collect(Collectors.toList());
    }

    /**
     * 管理员新增借阅记录
     * POST /borrow/add
     * 请求体示例：{"userId": 1, "collectionId": 10, "returnDeadline": "2025-07-01T00:00:00Z"}
     */
    @PostMapping("/add")
    public boolean addBorrow(@RequestBody BorrowAddRequest request) {
        Borrow borrow = new Borrow();
        borrow.setUserId(request.userId());
        borrow.setCollectionId(request.collectionId());
        borrow.setReturnDeadline(request.returnDeadline());
        return borrowService.createBorrow(borrow);
    }

    /**
     * 管理员更新借阅记录
     * PUT /borrow/update?borrowId=1
     * 请求体示例：{"renewedTimes": 1, "finePaid": 5.00, "returnDate": "2025-07-02T00:00:00Z", "collectionIsBorrowable": true}
     */
    @PutMapping("/update")
    public boolean updateBorrow(@RequestParam Long borrowId, @RequestBody BorrowUpdateRequest request) {
        Borrow borrow = new Borrow();
        borrow.setId(borrowId);
        borrow.setRenewedTimes(request.renewedTimes());
        borrow.setFinePaid(request.finePaid());
        borrow.setReturnDate(request.returnDate());
        return borrowService.updateBorrow(borrow, request.collectionIsBorrowable());
    }

    /**
     * 管理员删除借阅记录
     * DELETE /borrow/{id}
     */
    @DeleteMapping("/{id}")
    public boolean deleteBorrow(@PathVariable Long id) {
        return borrowService.deleteBorrow(id);
    }
}
