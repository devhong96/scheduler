package com.attendance.scheduler.infra.auth;

/**
 * 인증번호 검증 결과.
 */
public enum AuthVerification {
    /** 인증번호가 발급된 적이 없거나 세션에서 사라짐 */
    NOT_ISSUED,
    /** 인증번호 만료 */
    EXPIRED,
    /** 인증번호 불일치 */
    MISMATCH,
    /** 인증 성공 */
    SUCCESS
}