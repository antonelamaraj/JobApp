package amaraj.searchjob.application.mapper;

public interface BaseMapper<E, D> {
    E toEntity(D dto);
    D toDTO(E entity);
}
