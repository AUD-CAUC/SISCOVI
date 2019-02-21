package br.jus.stj.siscovi.helpers;

import br.jus.stj.siscovi.model.APIKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class JwtFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
       String jwt = containerRequestContext.getHeaderString("Authorization");
        try {
            if(jwt != null) {
                Claims claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(new APIKey().getSecret()))
                        .parseClaimsJws(jwt).getBody();
            }
        }catch (Exception ex) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
