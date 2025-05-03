document.querySelector('form').addEventListener('submit', function(event) {
    const startDate = document.getElementById('start_date').value;
    const endDate = document.getElementById('end_date').value;

    if (!startDate || !endDate) {
        alert('Por favor, preencha todas as datas.');
        event.preventDefault();
    } else if (new Date(startDate) > new Date(endDate)) {
        alert('A data inicial não pode ser maior que a data final.');
        event.preventDefault();
    }
});