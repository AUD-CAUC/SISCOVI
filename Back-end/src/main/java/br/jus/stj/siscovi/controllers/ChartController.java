package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.dao.ChartDAO;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

