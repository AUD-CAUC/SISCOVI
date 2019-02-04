package br.jus.stj.siscovi.controllers;

import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/courses")
public class CourseRestService {

    @GET
    @Path("/abc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourses() {
        List<Course> courses = new ArrayList<Course>();
        courses.add(new Course(1, "Configure Jersey with annotations"));
        courses.add(new Course(2, "Configure Jersey without web.xml"));
        Gson gson = new Gson();
        String json = gson.toJson(courses);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

}