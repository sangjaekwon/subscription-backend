package project.subscription.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.exception.ex.ExpiredJwtTokenException;
import project.subscription.exception.ex.InvalidJwtTokenException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if(header == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            jwtUtil.validate(token);
        } catch (ExpiredJwtException e) {
            responseThrow(response, """
                    {
                      "success": false,
                      "error": "토큰이 만료되었습니다."
                    }
                    """);
            return;
        } catch (JwtException e) {
            responseThrow(response, """
                    {
                      "success": false,
                      "error": "토큰이 유효하지 않습니다"
                    }
                    """);
            return;
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(jwtUtil.getUsername(token), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);

    }

    private static void responseThrow(HttpServletResponse response, String s) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(s);
    }
}
