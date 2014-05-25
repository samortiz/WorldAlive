package com.alwaysrejoice.worldalive;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Main Servlet
 */

public class WorldAliveServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<svg id='world' width='"+Const.MAX_X+"' height='"+Const.MAX_Y+"'>");
        
        World world = World.getWorld();
        for (Life life : world.getLives()) {
          int x = life.getX();
          int y = life.getY();
          String color = life.getColor();
          int radius = (int)life.getRadius();
          if (radius < 1) radius = 1;
          out.println("<circle cx='"+y+"' cy='"+x+"' r='"+radius+"' stroke='black' stroke-width='0.5' "+
              "fill='"+color+"' fill-opacity='0.5' />");
        }
        
        out.println("</svg>");
    }
}



