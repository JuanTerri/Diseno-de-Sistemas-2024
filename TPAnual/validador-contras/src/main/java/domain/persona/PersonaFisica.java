package domain.persona;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PersonaFisica extends Persona {

    @Column
    private String apellido;

    @Column
    private String sexo;
    @Column
    private String genero;

    @Column(columnDefinition = "DATE")
    private LocalDate fechaNacimiento;

    public PersonaFisica(
        String nombre,
        List<MedioDeContacto> mediosContacto,
        String direccion,
        Documento documento,
        String apellido,
        String sexo,
        String genero,
        LocalDate fechaNacimiento
        ){
        super(nombre, direccion, documento, mediosContacto);
        this.apellido = apellido;
        this.sexo = sexo;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombreCompleto() {
        return getNombre() + " " + getApellido();
    }

}
