import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
    thresholds: {
        http_req_failed: ['rate<0.01'], // Menos de 1% de falhas globais
    },
    vus: 10, // Usaremos 1 VU para percorrer a lógica sequencialmente
    duration: '10s',
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    // --- 1. TESTE: Criar Usuário Comum (Sucesso) ---
    const emailComum = `user${Math.random()}@teste.com`;
    const docComum = `DOC${Math.random()}`;
    
    let res = http.post(`${BASE_URL}/create-user`, JSON.stringify({
        fullName: "Usuario Comum",
        type: 1, // Tipo comum
        cadastrationCode: docComum,
        email: emailComum,
        password: "123"
    }), { headers: { 'Content-Type': 'application/json' } });

    check(res, { 'Usuário comum criado': (r) => r.status === 201 });
    const userComumId = res.headers['Location'] ? res.headers['Location'].split('/').pop() : null;

    // --- 2. TESTE: Criar Usuário Lojista (Sucesso) ---
    const emailLojista = `loja${Math.random()}@teste.com`;
    const docLojista = `CNPJ${Math.random()}`;
    
    res = http.post(`${BASE_URL}/create-user`, JSON.stringify({
        fullName: "Lojista S/A",
        type: 2, // Tipo Lojista (conforme seu validateRequest)
        cadastrationCode: docLojista,
        email: emailLojista,
        password: "123"
    }), { headers: { 'Content-Type': 'application/json' } });

    check(res, { 'Lojista criado': (r) => r.status === 201 });
    const lojistaId = res.headers['Location'] ? res.headers['Location'].split('/').pop() : null;

    // --- 3. TESTE: Criar Usuário Duplicado (Erro 409 Conflict) ---
    res = http.post(`${BASE_URL}/create-user`, JSON.stringify({
        fullName: "Duplicado",
        type: 1,
        cadastrationCode: docComum, // Mesmo documento do primeiro
        email: emailComum,
        password: "123"
    }), { headers: { 'Content-Type': 'application/json' } });

    check(res, { 'Erro duplicidade capturado (409)': (r) => r.status === 409 });

    // --- 4. TESTE: Criar Contas para os usuários ---
    // Conta Usuário Comum (Saldo 1000)
    http.post(`${BASE_URL}/create-account`, JSON.stringify({
        ownerId: userComumId,
        balance: 1000.00
    }), { headers: { 'Content-Type': 'application/json' } });

    // Conta Lojista (Saldo 0)
    http.post(`${BASE_URL}/create-account`, JSON.stringify({
        ownerId: lojistaId,
        balance: 0.00
    }), { headers: { 'Content-Type': 'application/json' } });

    sleep(1);

    // --- 5. TESTE: Transferência Sucesso (Comum -> Lojista) ---
    res = http.post(`${BASE_URL}/transfer`, JSON.stringify({
        payer: parseInt(userComumId),
        payee: parseInt(lojistaId),
        value: 100.00
    }), { headers: { 'Content-Type': 'application/json' } });

    check(res, { 
        'Transferência Comum -> Lojista OK': (r) => r.status === 202 || r.status === 200 
    });

    // --- 6. TESTE: Erro Negocial (Lojista tentando pagar) ---
    res = http.post(`${BASE_URL}/transfer`, JSON.stringify({
        payer: parseInt(lojistaId),
        payee: parseInt(userComumId),
        value: 10.00
    }), { headers: { 'Content-Type': 'application/json' } });

    // Aqui o Quarkus deve retornar erro conforme sua TransferException
    check(res, { 
        'Lojista não pode transferir (Erro)': (r) => r.status >= 400 
    });

    // --- 7. TESTE: Saldo Insuficiente ---
    res = http.post(`${BASE_URL}/transfer`, JSON.stringify({
        payer: parseInt(userComumId),
        payee: parseInt(lojistaId),
        value: 50000.00 // Mais do que o saldo
    }), { headers: { 'Content-Type': 'application/json' } });

    check(res, { 
        'Saldo insuficiente bloqueado': (r) => r.status >= 400 
    });

    // --- 8. TESTE: Consultar Saldo Final ---
    res = http.get(`${BASE_URL}/balance/${userComumId}`);
    check(res, {
        'Saldo atualizado corretamente': (r) => JSON.parse(r.body).balance === 900
    });
}