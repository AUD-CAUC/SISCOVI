package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.helpers.CORSFilter;
import br.jus.stj.siscovi.helpers.JwtFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class MyResource extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>(Arrays.asList(CORSFilter.class, JwtFilter.class, CourseRestService.class,Course.class,LoginController.class,
                RubricaController.class,ContratoController.class, UsuarioController.class, CargoController.class, PercentualController.class, ConvencoesController.class,
                VigenciaController.class, FuncionariosController.class, HistoricoController.class, TotalMensalController.class, FeriasController.class, RescisaoController.class,
                DecimoTerceiroController.class, SaldoController.class));
        return s;
    }
}