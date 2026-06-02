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
            writer.write("data: {\"step\": " + i + ", \"total\": 5, \"message\": \"处理中，已完成步骤 " + i + "/5\"}\n\n");
            writer.flush();

            // 检查客户端是否断开
            if (writer.checkError()) {
                break;
            }
        }

        // 发送完成消息
        if (!writer.checkError()) {
            writer.write("event: complete\n");
            writer.write("data: {\"code\": 0, \"message\": \"处理完成\", \"result\": \"这是最终的处理结果\"}\n\n");
            writer.flush();
        }
    }
}
