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

    private static final int SCALE = 2;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<svg id='world' width='"+Const.MAX_X*SCALE+"' height='"+Const.MAX_Y*SCALE+"'>");
        
        World world = World.getWorld();
        for (Life life : world.getLives()) {
          double radius = (life.getRadius() * SCALE);
          if (radius < 1) radius = 1;
          String svgFile = life.getSvgFile();

          if (svgFile != null) {
            // Draw an SVG Image
            int svgWidth = life.getSvgWidth();
            double svgScale = 2 * radius / svgWidth; 
            int imgX = (int)(((life.getX() * SCALE) - svgScale*(svgWidth/2))/svgScale);
            int imgY = (int)(((life.getY() * SCALE) - svgScale*(svgWidth/2))/svgScale);
            out.println("<image  transform='scale("+svgScale+")' x='"+imgX+"' y='"+imgY+"' width='"+svgWidth+"' height='"+svgWidth+"' xlink:href='images/"+svgFile+"' />");

          // Not an SVG image, use the default
          } else {
            String color = life.getColor();
            int x = life.getX() * SCALE;
            int y = life.getY() * SCALE;
            out.println("<circle class='life' cx='"+x+"' cy='"+y+"' r='"+radius+"' fill='"+color+"' />");
          }
        } // for
        
        
        out.println("</svg>");

    }
}



