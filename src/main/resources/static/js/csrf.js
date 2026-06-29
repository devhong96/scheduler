// 메타 태그의 CSRF 토큰을 모든 비-GET AJAX 요청 헤더에 자동 첨부한다.
// jQuery 로드 후, 페이지 고유 스크립트보다 먼저 포함해야 한다.
$(function () {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function (e, xhr, options) {
        if (!/^(GET|HEAD|OPTIONS|TRACE)$/i.test(options.type)) {
            xhr.setRequestHeader(header, token);
        }
    });
});