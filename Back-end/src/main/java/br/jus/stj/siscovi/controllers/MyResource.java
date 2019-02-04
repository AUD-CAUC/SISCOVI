package br.jus.stj.siscovi.controllers;

import br.jus.stj.siscovi.helpers.CORSFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;




@ApplicationPath("/rest")
public class MyResource extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>(Arrays.asList(CourseRestService.class,Course.class,LoginController.class, CORSFilter.class,
                RubricaController.class,ContratoController.class, UsuarioController.class, CargoController.class, PercentualController.class, ConvencoesController.class,
                VigenciaController.class, FuncionariosController.class, HistoricoController.class, TotalMensalController.class, FeriasController.class, RescisaoController.class, DecimoTerceiroController.class,
                SaldoController.class, SaldoResidualController.class));
        return s;
    }
}