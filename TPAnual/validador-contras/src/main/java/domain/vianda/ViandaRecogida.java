package domain.vianda;

import domain.heladera.Heladera;
import domain.rol.Vulnerable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import persistence.EntidadPersistente;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Date;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
public class ViandaRecogida extends EntidadPersistente {
  @OneToOne
  private Vulnerable necesitado;
  @OneToOne
  private Heladera heraderaDeVianda;
  @OneToOne(mappedBy = "viandaRecogida")
  private Vianda viandaRecogida;
  @Column(columnDefinition = "DATE")
  private Date fechaDeRecogida;
}
