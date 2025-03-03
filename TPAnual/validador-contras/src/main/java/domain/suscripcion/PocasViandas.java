package domain.suscripcion;

import domain.heladera.Heladera;
import domain.persona.MedioDeContacto;
import domain.servicios.TwilioSendGrid;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.IOException;

@Getter @Setter
@Entity
public class PocasViandas extends Suscripcion{



  @Column
  private Integer numeroMinimo;

  public PocasViandas() {

  }


  public boolean condicion(Integer cantidadViandas){
    return cantidadViandas<=numeroMinimo;
  }

  @Override
  void notificar() throws IOException {
      String subject =
              "Suscripcion a heladeraID " + this.getHeladera();
      String mensaje =
          "<h1>Hola <strong>" + this.getColaborador().getPersona().getNombre() + "</strong>,</h1>" +
              "<p>La heladera <strong>"+ this.getHeladera().getNombre() +"</strong>"+
              " tiene " + this.getHeladera().cantidadViandas()+ " viandas</p>";

      this.notificadores.notificar(subject, mensaje);

  }

  public PocasViandas(Heladera heladera, MedioDeContacto notificadores, Integer numeroMinimo){
    this.heladera=heladera;
    this.notificadores=notificadores;
    this.numeroMinimo = numeroMinimo;
    header="Notificacion por suscripcion";
    this.mensaje="Quedan únicamente "+numeroMinimo+" viandas disponibles en la heladera";
  }

  public PocasViandas(Heladera heladera) {
    this.heladera = heladera;
    this.notificadores=null;
    this.numeroMinimo=(Integer) (heladera.getTamanioEnViandas()/5);
    header="Notificacion por suscripcion";
    this.mensaje="Quedan únicamente "+numeroMinimo+" viandas disponibles en la heladera";
  }

  public PocasViandas(Heladera heladera, int numeroMinimo) {
    this.heladera = heladera;
    this.notificadores=null;
    this.numeroMinimo=numeroMinimo;
    header="Notificacion por suscripcion";
    this.mensaje="Quedan únicamente <strong>"+numeroMinimo+"</strong> viandas disponibles en la heladera";
  }
}
