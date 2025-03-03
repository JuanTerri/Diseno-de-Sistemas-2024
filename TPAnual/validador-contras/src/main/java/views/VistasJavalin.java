package views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import controllers.APIControllers;
import controllers.AuthController;
import controllers.BrokerControllers;
import domain.api.Localizacion;
import domain.auth.AccesoUsuarios;
import domain.auth.LinkMenu;
import domain.auth.PermisosMetodo;
import domain.servicios.Broker;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
import io.javalin.rendering.JavalinRenderer;
import persistence.ArchivosUtils;
import persistence.Demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;


public class VistasJavalin {
        public static void main(String[] args) throws JsonProcessingException {

            initTemplateEngine();

            //Demo.main(null);
            //Demo.personas(null);
            //Demo.colaboraciones(null);
            //Demo.servicios(null);

            JavalinJackson.defaultMapper();

            String portEnv = System.getenv("PORT");
            int port = (portEnv != null) ? Integer.parseInt(portEnv) : 8001;
            Javalin app = Javalin.create(config -> {
                config.staticFiles.add("/front/rentrega4");
            }).start(port);

            app.before(ctx -> {
                String path = ctx.path();
                String domain=System.getenv("DOMAIN");

                if (!path.equals("/broker/temperatura") && !path.equals("/broker/solicitarApertura") && !path.equals("/broker/sacarVianda")&&!path.equals("/api/temperatura") && !path.equals("/api/solicitarApertura") && !path.equals("/api/sacarVianda")) {
                    AccesoUsuarios.getInstance().revisarPermiso(ctx);
                }
            });


            app.get("/", ctx -> {
                ctx.redirect("/page-login");
            });

            app.get("/uploads/*", ctx -> {
                String subpath = ctx.path().substring("/uploads".length());
                Path imagePath = Paths.get(ArchivosUtils.getInstance().getUploadsPath(), subpath);
                if (Files.exists(imagePath)) {
                    ctx.contentType("image/");
                    ctx.result(Files.newInputStream(imagePath));
                } else {
                    ctx.status(404).result("Archivo no encontrado");
                }
            });

            AccesoUsuarios acceso = AccesoUsuarios.getInstance();

//====================== INDEX
            UI_Index UIndex = new UI_Index();

            app.get("/index", UIndex);
            acceso.agregarRuta(new PermisosMetodo("index", true, true, true, true));

            UI_Landing UILanding = new UI_Landing();
            app.get("/landing", UILanding);

//====================== PERFIL
            UI_Perfil UIPerfil = new UI_Perfil();
            app.get("/app-profile", UIPerfil);
            acceso.agregarRuta(new PermisosMetodo("app-profile", true, true, true, true));
            app.post("/actualizar-p", UIPerfil::actualizarColaboradorHumano);
            acceso.agregarRuta(new PermisosMetodo("actualizar-p", true, false, false, false));
            app.post("/actualizar-o", UIPerfil::actualizarColaboradorJuridico);
            acceso.agregarRuta(new PermisosMetodo("actualizar-o", false, true, false, false));
            app.post("/actualizar-t", UIPerfil::actualizarTecnico);
            acceso.agregarRuta(new PermisosMetodo("actualizar-t", false, false, true, false));

            app.post("/solicitar-tarjeta", UIPerfil::solicitarTarjeta);
            acceso.agregarRuta(new PermisosMetodo("solicitar-tarjeta", true, false, false, false));

//====================== VIANDA
            UI_Vianda UIVianda = new UI_Vianda();

            app.get("/vianda", UIVianda);
            app.post("/vianda", UIVianda::agregarVianda);
            acceso.agregarRuta(new LinkMenu(
                    "vianda",
                    "Donar Vianda",
                    "icon-cup",
                    true,
                    false,
                    false,
                    false
            ));

//====================== TRASLADO
            UI_Traslado UITraslado = new UI_Traslado();

            app.get("/traslado", UITraslado);
            app.post("/traslado", UITraslado::trasladarCantViandas);
            acceso.agregarRuta(new LinkMenu(
                    "traslado",
                    "Traslados",
                    "icon-direction",
                    true,
                    false,
                    false,
                    false
            ));

//====================== REGISTRAR PERSONAS
            UI_RegistrarPersona UIregistrarPersona = new UI_RegistrarPersona();

            app.get("/registrar-persona", UIregistrarPersona);
            app.post("/registrar-persona", UIregistrarPersona::agregarPersona);
            acceso.agregarRuta(new LinkMenu(
                    "registrar-persona",
                    "Registrar Personas",
                    "icon-user-follow",
                    true,
                    false,
                    false,
                    false
            ));

//====================== DINERO
            UI_Dinero UIDinero = new UI_Dinero();

            app.get("/dinero", UIDinero);
            app.post("/dinero", UIDinero::agregarDonacion);
            acceso.agregarRuta(new LinkMenu(
                    "dinero",
                    "Donar Dinero",
                    "icon-wallet",
                    true,
                    true,
                    false,
                    false
            ));

//====================== OFERTAS
            UI_Ofertas UIOfertas = new UI_Ofertas();

            app.get("/ofertas", UIOfertas);
            acceso.agregarRuta(new LinkMenu(
                    "ofertas",
                    "Ofertas",
                    "icon-present",
                    false,
                    true,
                    false,
                    false
            ));
            app.post("/eliminar-oferta", UIOfertas::eliminarOferta);
            acceso.agregarRuta(new PermisosMetodo("eliminar-oferta", false, true, false, false));

//====================== NUEVA OFERTA
            UI_Oferta UIOferta = new UI_Oferta();

            app.get("/oferta", UIOferta);
            acceso.agregarRuta(new PermisosMetodo("oferta", false, true, false, false));
            app.post("/nueva-oferta", UIOferta::nuevaOferta);
            acceso.agregarRuta(new PermisosMetodo("nueva-oferta", false, true, false, false));

//====================== HELADERAS-A
            UI_HeladerasA UIHeladerasA = new UI_HeladerasA();

            app.get("/heladeras-a", UIHeladerasA);
            acceso.agregarRuta(new LinkMenu(
                    "heladeras-a",
                    "Heladeras",
                    "icon-heladera",
                    false,
                    false,
                    false,
                    true
            ));
            app.post("/instalar-heladera", UIHeladerasA::instalarHeladera);
            acceso.agregarRuta(new PermisosMetodo("instalar-heladera", false, false, false, true));

//====================== HELADERAS-O
            UI_HeladerasO UIHeladerasO = new UI_HeladerasO();

            app.get("/heladeras-o", UIHeladerasO);
            acceso.agregarRuta(new LinkMenu(
                    "heladeras-o",
                    "Heladeras",
                    "icon-heladera",
                    false,
                    true,
                    false,
                    false
            ));

//====================== NUEVA HELADERA
            UI_NuevaHeladera UINuevaHeladera = new UI_NuevaHeladera();

            app.get("/nueva-heladera", UINuevaHeladera);
            app.post("/nueva-heladera", UINuevaHeladera::nuevaHeladera);
            acceso.agregarRuta(new PermisosMetodo("nueva-heladera", false, true, false, false));
            app.get("/puntos-sugeridos", UINuevaHeladera::obtenerPuntosSugeridos);
            acceso.agregarRuta(new PermisosMetodo("puntos-sugeridos", false, true, false, false));

//====================== HELADERAS-P
            UI_HeladerasP UIHeladerasP = new UI_HeladerasP();

            app.get("/heladeras-p", UIHeladerasP);
            app.post("/botonSuscribe", UIHeladerasP::botonSuscribe);
            acceso.agregarRuta(new PermisosMetodo(
                "botonSuscribe",
                true,
                false,
                false,
                false));
            acceso.agregarRuta(new LinkMenu(
                    "heladeras-p",
                    "Heladeras",
                    "icon-heladera",
                    true,
                    false,
                    false,
                    false
            ));

//====================== REPORTAR-FALLA
            UI_ReportarFalla UIReportarFalla = new UI_ReportarFalla();

            app.get("/reportar-falla", UIReportarFalla);
            app.post("/reportar-falla", UIReportarFalla::reportarFalla);
            acceso.agregarRuta(new PermisosMetodo("nueva-heladera", true, true, true, true));

//====================== VISITAS-TECNICAS
            UI_ListadoTecnicos UITecnicos = new UI_ListadoTecnicos();

            app.get("/listado-tecnicos", UITecnicos);
            acceso.agregarRuta(new LinkMenu(
                    "listado-tecnicos",
                    "Incidentes",
                    "icon-location-pin",
                    false,
                    false,
                    true,
                    false
            ));
            app.post("/asignar-tecnico", UITecnicos::asignarTecnico);
            acceso.agregarRuta(new PermisosMetodo("asignar-tecnico", false, false, true, true));

            UI_NuevaVisita UINuevaVisita = new UI_NuevaVisita();
            app.get("/nueva-visita", UINuevaVisita);
            app.post("/nueva-visita", UINuevaVisita::nuevaVisita);
            acceso.agregarRuta(new PermisosMetodo("nueva-visita", false, false, true, true));

//====================== INCIDENTES-ADMIN
            UI_IncidentesAdmin UIIncidentes = new UI_IncidentesAdmin();
            app.get("/incidentes-a", UIIncidentes);
            acceso.agregarRuta(new LinkMenu(
                    "incidentes-a",
                    "Incidentes",
                    "icon-location-pin",
                    false,
                    false,
                    false,
                    true
            ));
            app.post("/desasignar", UIIncidentes::desasignar);
            acceso.agregarRuta(new PermisosMetodo("desasignar", false, false, true, true));
            app.post("/forzar-solucion", UIIncidentes::forzarSolucion);
            acceso.agregarRuta(new PermisosMetodo("forzar-solucion", false, false, false, true));

//====================== PUNTOS
            UI_Puntos UIPuntos = new UI_Puntos();

            app.get("/puntos",UIPuntos);
            app.post("/puntos",UIPuntos::canjearPuntos);
            acceso.agregarRuta(new LinkMenu(
                    "puntos",
                    "Puntos",
                    "icon-diamond",
                    true,
                    true,
                    false,
                    false
            ));

//====================== REPORTES
            UI_Reporte UIrepo = new UI_Reporte();

            app.get("/reportes",UIrepo);
            app.post("/reporteSemana", UIrepo::mostrarReportePorSemana);
            acceso.agregarRuta(new LinkMenu(
                    "reportes",
                    "Reportes",
                    "icon-chart",
                    false,
                    false,
                    false,
                    true
            ));


//====================== MIGRACIÓN
            UI_Migracion UI_Migracion = new UI_Migracion();
            app.get("/migracion",UI_Migracion);
            app.post("/migracion", UI_Migracion::cargarArchivoCSV);
            acceso.agregarRuta(new LinkMenu(
                    "migracion",
                    "Migración",
                    "icon-cloud-upload",
                    false,
                    false,
                    false,
                    true
            ));

//====================== TECNICOS ADMIN
            UI_TecnicosA UITecnicosA = new UI_TecnicosA();
            app.get("/tecnicos-a", UITecnicosA);
            acceso.agregarRuta(new LinkMenu(
                    "tecnicos-a",
                    "Técnicos",
                    "icon-flag",
                    false,
                    false,
                    false,
                    true
            ));
            app.post("/habilitar-tecnico", UITecnicosA::habilitarTecnico);
            app.post("/deshabilitar-tecnico", UITecnicosA::deshabilitarTecnico);
            acceso.agregarRuta(new PermisosMetodo("habilitar-tecnico", false, false, false, true));
            acceso.agregarRuta(new PermisosMetodo("deshabilitar-tecnico", false, false, false, true));

//====================== LOGIN
            UI_Login UILogin = new UI_Login();

            app.get("/page-login", UILogin);
            app.post("/login", UILogin::login);
            app.post("/logout", UILogin::logout);

            app.get("/google-login", AuthController::loginWithGoogle);

            // Ruta de callback para recibir los resultados de Auth0
            app.get("/auth/callback", AuthController::handleCallback);

            // Ruta de callback para Google (en caso de usar Google OAuth a través de Auth0)
            app.get("/google/callback", AuthController::handleCallback);

//====================== REGISTRO
            UI_Registrar UIRegistrar = new UI_Registrar();

            app.get("/page-register", UIRegistrar);
            app.post("/registrar-p", UIRegistrar::registrarPersona);
            app.post("/registrar-o", UIRegistrar::registrarOrg);
            app.post("/registrar-t", UIRegistrar::registrarTecnico);
            app.post("/registrar-p-google", UIRegistrar::registrarPersonaGoogle);
            app.post("/registrar-o-google", UIRegistrar::registrarOrgGoogle);
            app.post("/registrar-t-google", UIRegistrar::registrarTecnicoGoogle);

//====================== API
            app.get("/api/localizacion", ctx -> {
                ctx.result(Localizacion.localizar(ctx));
            });


//====================== API nuestra que "interactua" con el broker y los servicios que interactuan con nosotros

            APIControllers api = new APIControllers();
            app.post("/api/temperatura",api::crearIncidenteTemperatura);

            app.post("/api/solicitarApertura", api::solicitarAperturaHeladera);

            app.post("/api/sacarVianda", api::sacarViandas);


//====================== API del Broker que seria el endpoint para los que interactuan con nosotros. TECNICAMENTE deberia de estar separado, es decir, ser levantado a parte en otro puerto y que sea una aplicacion a parte

            BrokerControllers broker = new BrokerControllers();
            app.post("/broker/temperatura",broker::incidenteTemperatura);

            app.post("/broker/sacarVianda", broker::sacarVianda);

            app.post("/broker/solicitarApertura", broker::solicitarAperturaHeladera);

//====================== NO ENCONTRADO
            app.error(404, ctx -> {
                System.out.println("Denegué en VistasJavalin");
                ctx.redirect("/denegado");
            });
        }

        private static void initTemplateEngine() {
            Handlebars handlebars = new Handlebars();

            handlebars.registerHelpers(new HelperSource());

            JavalinRenderer.register(
                (path, model, context) -> {
                    Template template = null;
                    try {
                        template = handlebars.compile("templates/" + path.replace(".hbs", ""));
                        return template.apply(model);
                    } catch (IOException e) {
                        //
                        e.printStackTrace();
                        context.status(HttpStatus.NOT_FOUND);
                        return "No se encuentra la pagina indicada...";
                    }
                }, ".hbs"
            );
        }

        private static Consumer<JavalinConfig> config() {
            return config -> {
                config.staticFiles.add(staticFiles -> {
                    staticFiles.hostedPath = "/";
                    staticFiles.directory = "/public";
                });
            };
        }

    }
