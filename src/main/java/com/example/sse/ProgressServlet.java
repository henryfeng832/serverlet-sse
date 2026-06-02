package com.example.sse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/progress")
public class ProgressServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/event-stream");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");

        PrintWriter writer = resp.getWriter();

        for (int i = 1; i <= 5; i++) {
            // 模拟处理耗时
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // 发送进度消息
            writer.write("event: progress\n");
            writer.write("data: {\"step\": " + i + ", \"total\": 5, \"message\": \"\u5904\u7406\u4e2d\uff0c\u5df2\u5b8c\u6210\u6b65\u9aa4 " + i + "/5\"}\n\n");
            writer.flush();

            // 检查客户端是否断开
            if (writer.checkError()) {
                break;
            }
        }

        // 发送完成消息
        if (!writer.checkError()) {
            writer.write("event: complete\n");
            writer.write("data: {\"code\": 0, \"message\": \"\u5904\u7406\u5b8c\u6210\", \"result\": \"\u8fd9\u662f\u6700\u7ec8\u7684\u5904\u7406\u7ed3\u679c\"}\n\n");
            writer.flush();
        }
    }
}