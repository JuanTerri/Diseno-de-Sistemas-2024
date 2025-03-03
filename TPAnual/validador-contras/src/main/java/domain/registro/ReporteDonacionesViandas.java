package domain.registro;

import domain.rol.Colaborador;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDonacionesViandas extends Reporte {
  private List<DonacionesColaborador> detalle;

  public int cantidadDonacionesPorColaborador(Colaborador colaborador) {
    return (int) detalle.stream()
            .filter(donacionesColaborador -> donacionesColaborador.getColaborador().equals(colaborador))
            .count();
  }
}
